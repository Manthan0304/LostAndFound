package com.lostandfound.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                // Local development
                                "http://localhost:3000",
                                "http://localhost:5173", 
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:5173",
                                
                                // v0 preview URLs
                                "https://*.lite.vusercontent.net",
                                
                                // Vercel deployments
                                "https://*.vercel.app",
                                
                                // Your specific frontend URL
                                frontendUrl,
                                
                                // Add your actual production frontend URL here
                                "https://your-frontend.vercel.app",
                                "https://lostandfound-production-a7ee.up.railway.app" // Your backend URL
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
                        .allowCredentials(true)
                        .maxAge(3600); // Cache preflight response for 1 hour
            }
        };
    }
}
