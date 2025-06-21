package com.zennest.payment.controller;

import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.repo.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * PaymentController handles endpoints related to payment transactions.
 * <p>
 * It provides:
 * <ul>
 *   <li>
 *     A health check endpoint (<code>GET /api/payments/health</code>) that returns a simple OK message to
 *     indicate the service is reachable.
 *   </li>
 *   <li>
 *     A count endpoint (<code>GET /api/payments/count</code>) that returns the number of payments with a
 *     specific status.
 *   </li>
 * </ul>
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentRepository paymentRepository;

    /**
     * Constructs a PaymentController with the required repository.
     *
     * @param paymentRepository the repository that handles payment data access
     */
    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Test endpoint to ensure service is reachable
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /**
     * Counts the number of payments with the specified status.
     * <p>
     * This endpoint retrieves the count of payment records that match the given status from the database.
     *
     * @param status the payment status to filter by
     * @return a ResponseEntity containing the count of payments with the specified status,
     * or a 500 status if an error occurs
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countByStatus(@RequestParam PaymentStatus status) {
        try {
            long count = paymentRepository.countByPaymentStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error counting payments by status", e);
            return ResponseEntity.status(500).build();
        }
    }
}
