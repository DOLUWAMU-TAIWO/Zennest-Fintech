package com.zennest.payment.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://vhsvcalumni.org",
                        "http://localhost:5173",
                        "https://qorelabs.online",
                        "https://qorelabs.xyz",
                        "https://qorelabs.space",
                        "https://qorelabs.store"
                )
                .allowedMethods("GET","POST","OPTIONS","PUT","DELETE")
                .allowedHeaders("Authorization","Content-Type","X-API-KEY")
                .allowCredentials(true)
                .maxAge(3600);
    }
}