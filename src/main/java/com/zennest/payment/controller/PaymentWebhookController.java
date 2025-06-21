package com.zennest.payment.controller;

import com.zennest.payment.Config.PaystackConfig;
import com.zennest.payment.metrics.PaymentMetricService;
import com.zennest.payment.model.Payment;
import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.model.PaystackConfirmationStatus;
import com.zennest.payment.model.Status;
import com.zennest.payment.repo.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

/**
 * Controller that handles Paystack webhook notifications for payment status updates.
 * <p>
 * This controller:
 * <ul>
 *   <li>Receives webhook notifications from Paystack payment gateway</li>
 *   <li>Verifies the authenticity of each webhook using HMAC SHA512 signatures</li>
 *   <li>Processes payment status updates (success, failed, abandoned)</li>
 *   <li>Updates the corresponding payment records in the database</li>
 *   <li>Collects metrics on webhook processing for monitoring</li>
 * </ul>
 * <p>
 * The webhook endpoint is secured by validating the signature provided in the
 * x-paystack-signature header against a computed signature using the Paystack secret key.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    /**
     * Logger for this class, used to record informational, warning, and error messages.
     */
    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookController.class);

    /**
     * Repository for accessing and manipulating Payment entities in the database.
     * Used to find and update payment records based on webhook data.
     */
    private final PaymentRepository paymentRepository;

    /**
     * Configuration containing Paystack API credentials and settings.
     * Provides the secret key used for webhook signature verification.
     */
    private final PaystackConfig paystackConfig;

    /**
     * JSON object mapper for serializing and deserializing webhook payload data.
     * Used to convert the webhook payload to a JSON string for signature verification.
     */
    private final ObjectMapper objectMapper;

    /**
     * Service for recording metrics related to payment processing.
     * Tracks webhook counts, processing times, and error rates.
     */
    private final PaymentMetricService metricService;

    /**
     * Constructs a new PaymentWebhookController with the required dependencies.
     *
     * @param paymentRepository repository for accessing and updating payment records
     * @param paystackConfig configuration containing Paystack API credentials and settings
     * @param objectMapper JSON object mapper for serializing and deserializing webhook payload data
     * @param metricService service for recording metrics related to payment processing
     */
    public PaymentWebhookController(PaymentRepository paymentRepository,
                                    PaystackConfig paystackConfig,
                                    ObjectMapper objectMapper,
                                    PaymentMetricService metricService) {
        this.paymentRepository = paymentRepository;
        this.paystackConfig = paystackConfig;
        this.objectMapper = objectMapper;
        this.metricService = metricService;
    }

    /**
     * Handles webhook notifications from Paystack payment gateway.
     * <p>
     * This method:
     * <ol>
     *   <li>Records metrics about webhook processing</li>
     *   <li>Verifies the webhook signature to ensure it came from Paystack</li>
     *   <li>Extracts payment details from the webhook payload</li>
     *   <li>Updates the corresponding payment record in the database</li>
     *   <li>Records processing time and returns a success response</li>
     * </ol>
     * <p>
     * If signature verification fails or the payload is invalid, appropriate error
     * responses are returned and metrics are updated to track these failures.
     *
     * @param signature the HMAC SHA512 signature from the x-paystack-signature header
     * @param payload the webhook payload containing payment details
     * @return a ResponseEntity with appropriate status code and message
     */
    @PostMapping("/webhook")
    public ResponseEntity<?> handleWebhook(@RequestHeader("x-paystack-signature") String signature,
                                           @RequestBody Map<String, Object> payload) {
        // Start timer and increment webhook counter
        long startTime = System.currentTimeMillis();
        metricService.incrementWebhookCounter();

        // Convert payload to JSON string
        String rawBody;
        try {
            rawBody = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            logger.error("Error converting payload to JSON", e);
            metricService.incrementWebhookErrorCounter();
            return ResponseEntity.status(500).body("Error processing webhook payload");
        }

        // Compute HMAC SHA512 of the raw payload using the Paystack secret key
        String computedSignature = computeHmacSHA512(rawBody, paystackConfig.getSecretKey());
        if (!computedSignature.equals(signature)) {
            logger.error("Invalid webhook signature: computed {} but received {}", computedSignature, signature);
            metricService.incrementWebhookErrorCounter();
            return ResponseEntity.status(400).body("Invalid signature");
        }

        // Process the webhook payload after signature verification
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        if (data == null) {
            logger.warn("Webhook payload missing 'data' field");
            metricService.incrementWebhookErrorCounter();
            return ResponseEntity.badRequest().body("Invalid payload: no data field");
        }

        String reference = (String) data.get("reference");
        String statusStr = (String) data.get("status");
        logger.info("Received webhook for reference: {} with status: {}", reference, statusStr);

        // Extract additional details from the payload
        String paystackTransactionId = String.valueOf(data.get("id")); // Numeric transaction id as string
        String gatewayResponse = (String) data.get("gateway_response");
        String channel = (String) data.get("channel");
        String currency = (String) data.get("currency");
        Integer fees = data.get("fees") instanceof Number ? ((Number) data.get("fees")).intValue() : null;
        String paidAtStr = (String) data.get("paid_at");

        // Retrieve the corresponding Payment record
        Optional<Payment> optionalPayment = paymentRepository.findByReference(reference);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            if ("success".equalsIgnoreCase(statusStr)) {
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setPaystackConfirmation(PaystackConfirmationStatus.CONFIRMED);
            } else if ("failed".equalsIgnoreCase(statusStr) || "abandoned".equalsIgnoreCase(statusStr)) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setPaystackConfirmation(PaystackConfirmationStatus.FAILED);
            }
            payment.setPaystackTransactionId(paystackTransactionId);
            payment.setGatewayResponse(gatewayResponse);
            payment.setChannel(channel);
            payment.setCurrency(currency);
            payment.setFees(fees);
            if (paidAtStr != null) {
                try {
                    payment.setPaidAt(java.time.OffsetDateTime.parse(paidAtStr).toLocalDateTime());
                } catch (Exception e) {
                    logger.error("Error parsing paid_at: {}", paidAtStr, e);
                }
            }
            paymentRepository.save(payment);
            logger.info("Updated payment {} with new status: {} and additional details", reference, payment.getPaymentStatus());
        } else {
            logger.warn("No payment found with reference: {}", reference);
        }

        long duration = System.currentTimeMillis() - startTime;
        metricService.recordWebhookProcessingTime(duration);
        return ResponseEntity.ok("Webhook processed");
    }

    /**
     * Computes the HMAC SHA512 signature for webhook verification.
     * <p>
     * This method:
     * <ol>
     *   <li>Creates an HMAC SHA512 instance</li>
     *   <li>Initializes it with the Paystack secret key</li>
     *   <li>Computes the hash of the webhook payload</li>
     *   <li>Returns the hash as a hexadecimal string</li>
     * </ol>
     * <p>
     * The computed signature is compared with the signature provided in the
     * webhook request header to verify the authenticity of the webhook.
     *
     * @param data the webhook payload as a JSON string
     * @param secret the Paystack secret key used for signature verification
     * @return the computed HMAC SHA512 signature as a hexadecimal string
     * @throws RuntimeException if an error occurs during signature computation
     */
    private String computeHmacSHA512(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            logger.error("Error computing HMAC SHA512", e);
            throw new RuntimeException("Error computing HMAC", e);
        }
    }
}
