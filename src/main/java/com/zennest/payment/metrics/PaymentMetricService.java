package com.zennest.payment.metrics;

import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.repo.PaymentRepository;
import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentMetricService {

    // Payment Initialization Metrics
    private final Counter paymentInitializationCounter;
    private final Timer paymentInitializationTimer;

    // Payment Verification Metrics
    private final Counter paymentVerificationCounter;
    private final Timer paymentVerificationTimer;
    private final PaymentRepository paymentRepository;
    // Webhook Metrics
    private final Counter webhookCounter;
    private final Counter webhookErrorCounter;
    private final Timer webhookProcessingTimer;

    // Payment Outcome Metrics
    private final Counter successfulPaymentCounter;
    private final Counter failedPaymentCounter;

    // Gauge for tracking current pending payments (this requires a callback or a method to update the value)
   // private final Gauge pendingPaymentsGauge;

    public PaymentMetricService(MeterRegistry meterRegistry, PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        // Payment initialization metrics
        this.paymentInitializationCounter = Counter.builder("payments.initialization.count")
                .description("Total number of payment initializations")
                .register(meterRegistry);
        this.paymentInitializationTimer = Timer.builder("payments.initialization.timer")
                .description("Time taken to initialize a payment")
                .register(meterRegistry);

        // Payment verification metrics
        this.paymentVerificationCounter = Counter.builder("payments.verification.count")
                .description("Total number of payment verifications")
                .register(meterRegistry);
        this.paymentVerificationTimer = Timer.builder("payments.verification.timer")
                .description("Time taken to verify a payment")
                .register(meterRegistry);

        // Webhook metrics
        this.webhookCounter = Counter.builder("payments.webhook.count")
                .description("Total number of webhook events processed")
                .register(meterRegistry);
        this.webhookErrorCounter = Counter.builder("payments.webhook.error.count")
                .description("Total number of webhook processing errors")
                .register(meterRegistry);
        this.webhookProcessingTimer = Timer.builder("payments.webhook.processing.timer")
                .description("Time taken to process a webhook event")
                .register(meterRegistry);

        // Payment outcome metrics
        this.successfulPaymentCounter = Counter.builder("payments.success.count")
                .description("Total number of successful payments")
                .register(meterRegistry);
        this.failedPaymentCounter = Counter.builder("payments.failed.count")
                .description("Total number of failed payments")
                .register(meterRegistry);

        // Example Gauge for pending payments:
//        // In a real application, you'd provide a method to update this value.
//        this.pendingPaymentsGauge = Gauge.builder("payments.pending.total", this, PaymentMetricService::getPendingPayments)
//                .description("Current number of pending payments")
//                .register(meterRegistry);
    }

    public double getPendingPayments() {
        return paymentRepository.countByPaymentStatus(PaymentStatus.PENDING);
    }

    // Methods to update metrics:
    public void incrementPaymentInitializationCounter() {
        paymentInitializationCounter.increment();
    }

    public void recordPaymentInitializationTime(long durationInMillis) {
        paymentInitializationTimer.record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void incrementPaymentVerificationCounter() {
        paymentVerificationCounter.increment();
    }

    public void recordPaymentVerificationTime(long durationInMillis) {
        paymentVerificationTimer.record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void incrementWebhookCounter() {
        webhookCounter.increment();
    }

    public void incrementWebhookErrorCounter() {
        webhookErrorCounter.increment();
    }

    public void recordWebhookProcessingTime(long durationInMillis) {
        webhookProcessingTimer.record(durationInMillis, TimeUnit.MILLISECONDS);
    }

    public void incrementSuccessfulPaymentCounter() {
        successfulPaymentCounter.increment();
    }

    public void incrementFailedPaymentCounter() {
        failedPaymentCounter.increment();
    }
}