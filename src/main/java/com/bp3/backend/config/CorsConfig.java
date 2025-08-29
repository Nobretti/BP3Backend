package com.bp3.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for allowing cross-origin requests from the frontend.
 * 
 * <p>This configuration allows the Angular frontend running on localhost:4200
 * to make requests to the Spring Boot backend API.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configure CORS for all endpoints.
     * 
     * @param registry CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "TRACE", "CONNECT")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1 hour
    }
}
