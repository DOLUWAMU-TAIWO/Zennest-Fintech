package com.zennest.payment.Config;

import com.zennest.payment.Filters.ApiKeyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private ApiKeyFilter apiKeyFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // exactly the same allowed‑origin list as user service
        config.setAllowedOriginPatterns(List.of(
                "https://vhsvcalumni.org",
                "http://localhost:5173",
                "https://qorelabs.online",
                "https://qorelabs.xyz",
                "https://qorelabs.space",
                "https://qorelabs.store"
        ));
        config.setAllowedMethods(List.of("GET","POST","OPTIONS","PUT","DELETE"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-API-KEY"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())        // picks up corsConfigurationSource()
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // public endpoints
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/prometheus",
                                "/api/payments/health",
                                "/api/payments/**"
                        ).permitAll()
                        // everything else needs a valid API key
                        .anyRequest().authenticated()
                )
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}