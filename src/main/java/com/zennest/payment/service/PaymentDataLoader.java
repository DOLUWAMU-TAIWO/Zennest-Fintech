package com.zennest.payment.service;

import com.zennest.payment.model.Payment;
import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.model.PaymentType;
import com.zennest.payment.repo.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
public class PaymentDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentDataLoader.class);
    private final PaymentRepository paymentRepository;

    public PaymentDataLoader(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paymentRepository.count() == 0) {  // Check if the repository is empty
            Payment payment1 = new Payment("user1@example.com", 100, PaymentStatus.PENDING, PaymentType.BOOKING);
            Payment payment2 = new Payment("user2@example.com", 200, PaymentStatus.SUCCESS, PaymentType.RENT);
            Payment payment3 = new Payment("user3@example.com", 300, PaymentStatus.FAILED, PaymentType.SALES);
            Payment payment4 = new Payment("user4@example.com", 400, PaymentStatus.PENDING, PaymentType.MEMBERSHIP);
            Payment payment5 = new Payment("user5@example.com", 500, PaymentStatus.SUCCESS, PaymentType.BOOKING);
            Payment payment6 = new Payment("user6@example.com", 600, PaymentStatus.FAILED, PaymentType.RENT);

            paymentRepository.saveAll(Arrays.asList(payment1, payment2, payment3, payment4, payment5, payment6));  // Save all payments to the repository
            log.info("Loaded 6 payments into the database.");
        } else {
            log.info("Not loading payments because the repository contains data.");
        }
    }
}