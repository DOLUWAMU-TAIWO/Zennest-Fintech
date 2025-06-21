package com.zennest.payment.controller;

import com.zennest.payment.DTO.ResolveAccountRequest;
import com.zennest.payment.DTO.ResolveAccountResponse;
import com.zennest.payment.service.PaystackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/payment")
public class BankController {
    private static final Logger logger = LoggerFactory.getLogger(BankController.class);
    private final PaystackService paystackService;

    public BankController(PaystackService paystackService) {
        this.paystackService = paystackService;
    }

    @GetMapping("/banks")
    public ResponseEntity<?> getBanks() {
        logger.info("BankController.getBanks() called");
        return ResponseEntity.ok(paystackService.getBanks());
    }

    @PostMapping("/payout-profile/resolve")
    public ResponseEntity<ResolveAccountResponse> resolveAccount(@RequestBody ResolveAccountRequest request) {
        logger.info("BankController.resolveAccount() called with user id: {}", request.getUserId());
        ResolveAccountResponse response = paystackService.resolveAccount(request);
        return ResponseEntity.ok(response);
    }
}
