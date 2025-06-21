package com.zennest.payment.repo;

import com.zennest.payment.model.Payment;
import com.zennest.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReference(String reference);

    // Count payments by a specific status (PENDING, SUCCESS, or FAILED)
    long countByPaymentStatus(PaymentStatus paymentStatus);

    // Retrieve all payments with a given status.
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
}