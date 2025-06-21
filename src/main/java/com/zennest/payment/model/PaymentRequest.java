package com.zennest.payment.model;

public class PaymentRequest {

    // Customer's email, used by Paystack to identify the payer
    private String email;

    // Amount in kobo (Paystack's requirement, e.g., 500000 = 5000 NGN)
    private int amount;

    // Getter and Setter for email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter and Setter for amount
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
