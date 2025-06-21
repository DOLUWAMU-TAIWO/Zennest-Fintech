// File: src/main/java/dev/dolu/payment/Filters/ApiKeyFilter.java
package com.zennest.payment.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Value("${service.password}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Bypass API key filter for all actuator endpoints and the payments health check
        if (uri.startsWith("/actuator") || "/api/payments/health".equals(uri)) {
            logger.info("Bypassing API key filter for endpoint: {}", uri);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Unauthorized request – Missing or invalid Authorization header from IP: {}",
                    request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String providedApiKey = header.substring(7);
        if (!apiKey.equals(providedApiKey)) {
            logger.warn("Unauthorized request – Invalid API Key from IP: {}",
                    request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API Key");
            return;
        }

        logger.info("Successful authentication via API Key from IP: {}",
                request.getRemoteAddr());

        var auth = new UsernamePasswordAuthenticationToken(
                "apiKeyUser", null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}