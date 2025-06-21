package com.zennest.payment.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResolveAccountResponse {
    private String id;
    private String name;
    private String type;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String currency;
    private String recipientCode;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
    private Object details;

    public ResolveAccountResponse() {}

    public ResolveAccountResponse(String id, String name, String type, String accountNumber, String bankCode, String bankName, String currency, String recipientCode, Boolean active, String createdAt, String updatedAt, Object details) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.currency = currency;
        this.recipientCode = recipientCode;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.details = details;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getRecipientCode() { return recipientCode; }
    public void setRecipientCode(String recipientCode) { this.recipientCode = recipientCode; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public Object getDetails() { return details; }
    public void setDetails(Object details) { this.details = details; }
}
