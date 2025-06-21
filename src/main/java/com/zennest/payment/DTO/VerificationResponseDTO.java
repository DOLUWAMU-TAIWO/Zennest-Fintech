package com.zennest.payment.DTO;

/**
 * VerificationResponseDTO provides a simple response to the frontend,
 * containing the final status of the payment and a message.
 */
public class VerificationResponseDTO {

    private String status;
    private String message;

    public VerificationResponseDTO() {
    }

    public VerificationResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}