package com.zennest.payment.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaystackConfig {

    // Public getter for the secret key
    // Injecting the Paystack secret key from application.properties
    @Value("${paystack.secret.key}")
    private String secretKey;

    // Public getter for the base URL
    // Injecting the base URL for Paystack API
    @Value("${paystack.api.base.url}")
    private String baseUrl;
    public String getSecretKey() {
        return secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}

