package com.zennest.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        @NotEmpty
        @Column(nullable = false)
        private String email;

        @Positive
        @Column(nullable = false)
        private Integer amount;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentStatus paymentStatus;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentType paymentType;

        // Stores the user's UUID if the donor is a registered member
        @Column(name = "user_id")
        private UUID userId;

        // Stores the Paystack transaction reference (returned during initialization)
        @Column(name = "reference")
        private String reference;

        // Stores the Paystack transaction ID (returned during verification)
        @Column(name = "paystack_transaction_id")
        private String paystackTransactionId;

        // Additional verification details
        @Column(name = "gateway_response")
        private String gatewayResponse;  // e.g., "[Test] Approved"

        @Column(name = "paid_at")
        private LocalDateTime paidAt;  // When the payment was processed

        @Column(name = "channel")
        private String channel;  // e.g., "card"

        @Column(name = "currency")
        private String currency;  // e.g., "NGN"

        @Column(name = "fees")
        private Integer fees;  // The fees charged by Paystack

        // New field: Stores the definitive confirmation status from Paystack
        @Enumerated(EnumType.STRING)
        @Column(name = "paystack_confirmation", nullable = false)
        private PaystackConfirmationStatus paystackConfirmation = PaystackConfirmationStatus.UNCONFIRMED;

        @Column(nullable = false)
        private LocalDateTime createdAt;

        @Column(nullable = false)
        private LocalDateTime updatedAt;

        // Stores the property ID for which the payment is made (e.g., for bookings, sales)
        @Column(name = "property_id")
        private UUID propertyId;

        protected Payment() {
                // Required by JPA
        }

        // Remove donation-specific constructors
        public Payment(String email, Integer amount, PaymentStatus paymentStatus, UUID userId, PaymentType paymentType, UUID propertyId) {
                if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                }
                this.email = email;
                this.amount = amount;
                this.paymentStatus = paymentStatus;
                this.userId = userId;
                this.paymentType = paymentType;
                this.propertyId = propertyId;
                this.paystackConfirmation = PaystackConfirmationStatus.UNCONFIRMED;
        }

        // General constructor (for membership, booking, rent, sales)
        public Payment(String email, Integer amount, PaymentStatus paymentStatus, PaymentType paymentType) {
                this(email, amount, paymentStatus, null, paymentType, null);
        }

        // Getters and setters for all fields

        public UUID getId() { return id; }
        public String getEmail() { return email; }
        public Integer getAmount() { return amount; }
        public PaymentStatus getPaymentStatus() { return paymentStatus; }
        public UUID getUserId() { return userId; }
        public String getReference() { return reference; }
        public String getPaystackTransactionId() { return paystackTransactionId; }
        public String getGatewayResponse() { return gatewayResponse; }
        public LocalDateTime getPaidAt() { return paidAt; }
        public String getChannel() { return channel; }
        public String getCurrency() { return currency; }
        public Integer getFees() { return fees; }
        public PaystackConfirmationStatus getPaystackConfirmation() { return paystackConfirmation; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public PaymentType getPaymentType() { return paymentType; }
        public UUID getPropertyId() { return propertyId; }

        public void setEmail(String email) { this.email = email; }
        public void setAmount(Integer amount) {
                if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                }
                this.amount = amount;
        }
        public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
        public void setUserId(UUID userId) { this.userId = userId; }
        public void setReference(String reference) { this.reference = reference; }
        public void setPaystackTransactionId(String paystackTransactionId) { this.paystackTransactionId = paystackTransactionId; }
        public void setGatewayResponse(String gatewayResponse) { this.gatewayResponse = gatewayResponse; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
        public void setChannel(String channel) { this.channel = channel; }
        public void setCurrency(String currency) { this.currency = currency; }
        public void setFees(Integer fees) { this.fees = fees; }
        public void setPaystackConfirmation(PaystackConfirmationStatus paystackConfirmation) { this.paystackConfirmation = paystackConfirmation; }
        public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }
        public void setPropertyId(UUID propertyId) { this.propertyId = propertyId; }

        @PrePersist
        protected void onCreate() {
                createdAt = LocalDateTime.now();
                updatedAt = createdAt;
        }

        @PreUpdate
        protected void onUpdate() {
                updatedAt = LocalDateTime.now();
        }

        public void setId(UUID paymentId) {
        }
}
