package com.hotel.gateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    // Endpoints that do NOT require a JWT token
    public static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/validate",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/auth-service/v3/api-docs",
            "/customer-service/v3/api-docs",
            "/room-service/v3/api-docs",
            "/booking-service/v3/api-docs",
            "/payment-service/v3/api-docs",
            "/webjars/",
            "/fallback"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> OPEN_API_ENDPOINTS.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
