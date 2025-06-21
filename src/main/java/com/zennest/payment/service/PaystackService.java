package com.zennest.payment.service;

import com.zennest.payment.Config.PaystackConfig;
import com.zennest.payment.model.PaymentRequest;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zennest.payment.DTO.ResolveAccountRequest;
import com.zennest.payment.DTO.ResolveAccountResponse;

@Service
public class PaystackService {

    private final PaystackConfig paystackConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PaystackService(PaystackConfig paystackConfig) {
        this.paystackConfig = paystackConfig;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> initializePayment(PaymentRequest paymentRequest) throws Exception {
        URI uri = URI.create(paystackConfig.getBaseUrl() + "/transaction/initialize");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", paymentRequest.getEmail());
        requestBody.put("amount", paymentRequest.getAmount());
        requestBody.put("callback_url", "https://qorelabs.xyz/verify-payment");  // Add callback URL here

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + paystackConfig.getSecretKey())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), Map.class);
        
    }
    public Map<String, Object> verifyPayment(String reference) throws Exception {
        URI uri = URI.create(paystackConfig.getBaseUrl() + "/transaction/verify/" + reference);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Bearer " + paystackConfig.getSecretKey())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Map<String, Object> jsonResponse = objectMapper.readValue(response.body(), Map.class);
        boolean rootStatus = (boolean) jsonResponse.get("status");
        Map<String, Object> responseData = (Map<String, Object>) jsonResponse.get("data");
        String transactionStatus = responseData != null ? (String) responseData.get("status") : null;

        Map<String, Object> result = new HashMap<>();
        if (rootStatus && "success".equalsIgnoreCase(transactionStatus)) {
            result.put("status", true);
            result.put("message", "Payment verified successfully");
            result.put("data", responseData);
        } else if ("abandoned".equalsIgnoreCase(transactionStatus) || "failed".equalsIgnoreCase(transactionStatus)) {
            result.put("status", false);
            result.put("message", "Payment was not completed. Please try again.");
        } else {
            result.put("status", false);
            result.put("message", "Payment verification failed.");
        }
        return result;
    }

    public Map<String, Object> getBanks() {
        try {
            URI uri = URI.create(paystackConfig.getBaseUrl() + "/bank");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Authorization", "Bearer " + paystackConfig.getSecretKey())
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), Map.class);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", false);
            error.put("message", "Failed to fetch banks: " + e.getMessage());
            return error;
        }
    }

    public ResolveAccountResponse resolveAccount(ResolveAccountRequest request) {
        try {
            // Step 1: Resolve account
            URI resolveUri = URI.create(paystackConfig.getBaseUrl() + "/bank/resolve?account_number=" + request.getAccountNumber() + "&bank_code=" + request.getBankCode());
            HttpRequest resolveRequest = HttpRequest.newBuilder()
                    .uri(resolveUri)
                    .header("Authorization", "Bearer " + paystackConfig.getSecretKey())
                    .GET()
                    .build();
            HttpResponse<String> resolveResponse = httpClient.send(resolveRequest, HttpResponse.BodyHandlers.ofString());
            Map<?,?> resolveResult = objectMapper.readValue(resolveResponse.body(), Map.class);
            Map<?,?> resolveData = (Map<?,?>) resolveResult.get("data");
            String accountName = resolveData != null ? (String) resolveData.get("account_name") : null;

            // Step 2: Create transfer recipient
            Map<String, Object> recipientPayload = new HashMap<>();
            recipientPayload.put("type", "nuban");
            recipientPayload.put("name", accountName);
            recipientPayload.put("account_number", request.getAccountNumber());
            recipientPayload.put("bank_code", request.getBankCode());
            recipientPayload.put("currency", "NGN");
            String recipientBody = objectMapper.writeValueAsString(recipientPayload);
            URI recipientUri = URI.create(paystackConfig.getBaseUrl() + "/transferrecipient");
            HttpRequest recipientRequest = HttpRequest.newBuilder()
                    .uri(recipientUri)
                    .header("Authorization", "Bearer " + paystackConfig.getSecretKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(recipientBody))
                    .build();
            HttpResponse<String> recipientResponse = httpClient.send(recipientRequest, HttpResponse.BodyHandlers.ofString());
            Map<?,?> recipientResult = objectMapper.readValue(recipientResponse.body(), Map.class);
            Map<?,?> recipientData = (Map<?,?>) recipientResult.get("data");

            // Extract all available fields
            String id = recipientData != null && recipientData.get("id") != null ? recipientData.get("id").toString() : null;
            String name = recipientData != null ? (String) recipientData.get("name") : null;
            String type = recipientData != null ? (String) recipientData.get("type") : null;
            String accountNumber = recipientData != null ? (String) recipientData.get("account_number") : null;
            String bankCode = recipientData != null ? (String) recipientData.get("bank_code") : null;
            String bankName = recipientData != null ? (String) recipientData.get("bank_name") : null;
            String currency = recipientData != null ? (String) recipientData.get("currency") : null;
            String recipientCode = recipientData != null ? (String) recipientData.get("recipient_code") : null;
            Boolean active = recipientData != null ? (Boolean) recipientData.get("active") : null;
            String createdAt = recipientData != null && recipientData.get("createdAt") != null ? recipientData.get("createdAt").toString() : null;
            String updatedAt = recipientData != null && recipientData.get("updatedAt") != null ? recipientData.get("updatedAt").toString() : null;
            Object details = recipientData != null ? recipientData.get("details") : null;

            return new ResolveAccountResponse(id, name, type, accountNumber, bankCode, bankName, currency, recipientCode, active, createdAt, updatedAt, details);
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve account or create recipient", e);
        }
    }
}