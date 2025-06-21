package com.zennest.payment.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class SalesPaymentRequest {
    @NotNull
    private String userId;

    @NotNull
    private String propertyId;

    @Email
    @NotNull
    private String email;

    @Min(100)
    private int amount;

    @NotNull
    private String transactionReference;

    @NotNull
    private PaymentType paymentType; // SALES

    @NotNull
    private PaymentStatus paymentStatus;

    @NotNull
    private PaystackConfirmationStatus confirmationStatus;

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public PaymentType getPaymentType() { return paymentType; }
    public void setPaymentType(PaymentType paymentType) { this.paymentType = paymentType; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public PaystackConfirmationStatus getConfirmationStatus() { return confirmationStatus; }
    public void setConfirmationStatus(PaystackConfirmationStatus confirmationStatus) { this.confirmationStatus = confirmationStatus; }
}
