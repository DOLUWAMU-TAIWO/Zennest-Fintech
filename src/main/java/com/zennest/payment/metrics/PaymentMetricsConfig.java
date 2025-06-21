package com.zennest.payment.metrics;

import com.zennest.payment.model.Status;
import com.zennest.payment.repo.PaymentRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PaymentMetricsConfig {

    private final PaymentRepository paymentRepository;

    public PaymentMetricsConfig(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Payment initialization metrics
    @Bean
    public Counter paymentInitializationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.initialization.count")
                .description("Total number of payment initializations")
                .register(meterRegistry);
    }

    @Bean
    public Timer paymentInitializationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("payments.initialization.timer")
                .description("Time taken to initialize a payment")
                .register(meterRegistry);
    }

    // Payment verification metrics
    @Bean
    public Counter paymentVerificationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.verification.count")
                .description("Total number of payment verifications")
                .register(meterRegistry);
    }

    @Bean
    public Timer paymentVerificationTimer(MeterRegistry meterRegistry) {
        return Timer.builder("payments.verification.timer")
                .description("Time taken to verify a payment")
                .register(meterRegistry);
    }

    // Webhook metrics
    @Bean
    public Counter webhookCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.webhook.count")
                .description("Total number of webhook events processed")
                .register(meterRegistry);
    }

    @Bean
    public Counter webhookErrorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.webhook.error.count")
                .description("Total number of webhook processing errors")
                .register(meterRegistry);
    }

    @Bean
    public Timer webhookProcessingTimer(MeterRegistry meterRegistry) {
        return Timer.builder("payments.webhook.processing.timer")
                .description("Time taken to process a webhook event")
                .register(meterRegistry);
    }

    // Payment outcome metrics
    @Bean
    public Counter successfulPaymentCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.success.count")
                .description("Total number of successful payments")
                .register(meterRegistry);
    }

    @Bean
    public Counter failedPaymentCounter(MeterRegistry meterRegistry) {
        return Counter.builder("payments.failed.count")
                .description("Total number of failed payments")
                .register(meterRegistry);
    }

    // Gauge for tracking current pending payments
//    @Bean
//    public Gauge pendingPaymentsGauge(MeterRegistry meterRegistry) {
//        return Gauge.builder("payments.pending.total", paymentRepository,
//                        repo -> repo.countByStatus(Status.PENDING))
//                .description("Current number of pending payments")
//                .register(meterRegistry);
//    }
}