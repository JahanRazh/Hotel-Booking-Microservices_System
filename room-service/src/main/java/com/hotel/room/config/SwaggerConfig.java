package com.hotel.room.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI roomServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room Service API")
                        .description("Hotel Management System - Room Management Service")
                        .version("1.0.0")
                        .contact(new Contact().name("Hotel Management Team")));
    }
}
