package com.example.GraduationProject.WebApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // List of allowed origins
        corsConfiguration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "http://localhost:3002",
                "http://localhost:3003",
                "http://localhost:3004",
                "http://localhost:3005",
                "http://localhost:5500",
                "http://127.0.0.1:3000",
                "http://127.0.0.1:3001",
                "http://127.0.0.1:5500",
                "http://10.0.2.2:8080",  // Android Emulator
                "http://192.168.1.x:8080" // Local network IP (replace with your IP)
        ));

        // Allowed HTTP methods
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allowed headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (important for Authorization headers, cookies, etc.)
        corsConfiguration.setAllowCredentials(true);

        // Expose headers (if you need the frontend to access specific headers)
        corsConfiguration.addExposedHeader("Authorization");

        // Register the CORS configuration for all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}
