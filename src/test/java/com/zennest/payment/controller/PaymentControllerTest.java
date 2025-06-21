package com.zennest.payment.controller;

import com.zennest.payment.model.PaymentStatus;
import com.zennest.payment.repo.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnHealthResponse() throws Exception {
        this.mockMvc.perform(get("/api/payments/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));
    }

    @Test
    public void shouldReturnCountByStatus() throws Exception {
        // This test assumes you have a way to mock PaymentRepository and set up test data if needed.
        // You may need to add @MockBean for PaymentRepository and mock its countByPaymentStatus method.
        // For now, this is a simple endpoint test.
        this.mockMvc.perform(get("/api/payments/count?status=PENDING"))
                .andDo(print())
                .andExpect(status().isOk());
    }

}