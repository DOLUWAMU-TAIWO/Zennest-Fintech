package com.zennest.payment.controller;

import com.zennest.payment.model.Payment;
import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.repo.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

@Controller
public class PaymentGraphqlController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentGraphqlController.class);

    private final PaymentRepository paymentRepository;

    public PaymentGraphqlController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Query to retrieve a payment by its unique ID.
    @QueryMapping
    public Payment getPaymentById(@Argument UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with id: " + id));
    }

    // Query to retrieve a payment using its Paystack reference.
    @QueryMapping
    public Payment getPaymentByReference(@Argument String reference) {
        return paymentRepository.findByReference(reference)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with reference: " + reference));
    }

    // Query to list all payments.
    @QueryMapping
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No payments found.");
        }
        return payments;
    }

    // Query to list payments filtered by status.
    @QueryMapping
    public List<Payment> getPaymentsByStatus(@Argument PaymentStatus status) {
        List<Payment> payments = paymentRepository.findByPaymentStatus(status);
        if (payments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No payments found with status: " + status);
        }
        return payments;
    }

    // Query to count the payments with a specific status.
    @QueryMapping
    public int countPaymentsByStatus(@Argument PaymentStatus status) {
        return (int) paymentRepository.countByPaymentStatus(status);
    }
}