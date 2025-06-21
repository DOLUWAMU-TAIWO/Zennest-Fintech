// java
package com.zennest.payment;

import com.zennest.payment.client.UserGraphQLClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class PaymentApplicationTests {

    @MockBean
    private UserGraphQLClient userGraphQLClient;

    @Test
    void contextLoads() {
    }
}