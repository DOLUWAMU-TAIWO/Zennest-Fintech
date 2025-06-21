package com.zennest.payment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;
import java.util.UUID;

@Service
public class UserGraphQLClient {

    private static final Logger logger = LoggerFactory.getLogger(UserGraphQLClient.class);
    private final HttpGraphQlClient graphQlClient;

    /**
     * Constructs a new UserGraphQLClient.
     *
     * @param webClientBuilder the WebClient builder
     * @param apiKey           the API key for the user service, injected from configuration
     */
    public UserGraphQLClient(WebClient.Builder webClientBuilder,
                             @Value("${userservice.api.key}") String apiKey) {
        WebClient client = webClientBuilder
                .baseUrl("https://qorelabs.online/graphql")
                .defaultHeader("X-API-KEY", apiKey)
                .build();
        this.graphQlClient = HttpGraphQlClient.builder(client).build();
    }

    /**
     * Retrieves the UUID of a user by their email address by sending a GraphQL query.
     *
     * Expected JSON response from the User Service:
     * {
     *   "data": {
     *     "getUserByEmail": {
     *       "id": "50c61e0d-b2f5-4cf5-af4e-d588a64a01c1"
     *     }
     *   }
     * }
     *
     * @param email the user's email address
     * @return the UUID of the user, or null if not found
     */
    @SuppressWarnings("unchecked")
    public UUID getUserUUIDByEmail(String email) {
        String query = """
            query getUserByEmail($email: String!) {
                getUserByEmail(email: $email) {
                    id
                }
            }
            """;
        try {
            // Send the query and retrieve the "getUserByEmail" field from the JSON response as a Map.
            Map<String, Object> userMap = (Map<String, Object>) graphQlClient.document(query)
                    .variable("email", email)
                    .retrieve("getUserByEmail")
                    .toEntity(Map.class)
                    .block();
            logger.info("GraphQL response for email {}: {}", email, userMap);
            if (userMap == null || userMap.get("id") == null) {
                return null;
            }
            String idStr = (String) userMap.get("id");
            return UUID.fromString(idStr);
        } catch (Exception e) {
            logger.error("Error retrieving user by email: {}", email, e);
            return null;
        }
    }
}