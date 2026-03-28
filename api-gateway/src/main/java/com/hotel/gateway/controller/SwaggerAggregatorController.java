package com.hotel.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SwaggerAggregatorController {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final Map<String, String> SERVICE_URLS = Map.of(
            "Auth", "http://localhost:8081/v3/api-docs",
            "Customer", "http://localhost:8082/v3/api-docs",
            "Room", "http://localhost:8083/v3/api-docs",
            "Booking", "http://localhost:8084/v3/api-docs",
            "Payment", "http://localhost:8085/v3/api-docs"
    );

    public SwaggerAggregatorController(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @GetMapping(value = "/v3/api-docs/aggregated", produces = "application/json")
    public Mono<String> getAggregatedSwagger() {
        return Flux.fromIterable(SERVICE_URLS.entrySet())
                .flatMap(entry -> fetchAndRename(entry.getKey(), entry.getValue()))
                .collectList()
                .map(this::mergeOpenApis);
    }

    private Mono<JsonNode> fetchAndRename(String serviceName, String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    // Prefix all schemas to avoid collision: #/components/schemas/Name -> #/components/schemas/ServiceName_Name
                    String modifiedJson = json.replace("#/components/schemas/", "#/components/schemas/" + serviceName + "_");
                    try {
                        JsonNode node = objectMapper.readTree(modifiedJson);
                        renameSchemasInComponents(serviceName, node);
                        return node;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing JSON from " + serviceName, e);
                    }
                })
                .onErrorResume(e -> {
                    System.err.println("Failed to fetch docs for " + serviceName + ": " + e.getMessage());
                    return Mono.empty();
                });
    }

    private void renameSchemasInComponents(String serviceName, JsonNode root) {
        JsonNode components = root.get("components");
        if (components != null && components.has("schemas")) {
            ObjectNode schemas = (ObjectNode) components.get("schemas");
            List<String> keys = new ArrayList<>();
            schemas.fieldNames().forEachRemaining(keys::add);
            
            for (String key : keys) {
                JsonNode schemaNode = schemas.get(key);
                schemas.remove(key);
                schemas.set(serviceName + "_" + key, schemaNode);
            }
        }
    }

    private String mergeOpenApis(List<JsonNode> nodes) {
        if (nodes.isEmpty()) return "{}";

        ObjectNode master = objectMapper.createObjectNode();
        master.put("openapi", "3.0.1");
        
        ObjectNode info = master.putObject("info");
        info.put("title", "Hotel Management System — Aggregated API");
        info.put("description", "Unified view of all hotel microservices. [ADMIN ONLY] and [USER] tags indicate access levels.");
        info.put("version", "1.0.0");

        ObjectNode paths = master.putObject("paths");
        ObjectNode components = master.putObject("components");
        ObjectNode schemas = components.putObject("schemas");
        ObjectNode securitySchemes = components.putObject("securitySchemes");
        
        // Add Bearer Security Scheme to master
        ObjectNode bearerScheme = securitySchemes.putObject("bearer-jwt");
        bearerScheme.put("type", "http");
        bearerScheme.put("scheme", "bearer");
        bearerScheme.put("bearerFormat", "JWT");

        // Global security requirement
        master.putArray("security").addObject().putArray("bearer-jwt");

        for (JsonNode node : nodes) {
            // Merge Paths
            JsonNode servicePaths = node.get("paths");
            if (servicePaths != null) {
                servicePaths.fields().forEachRemaining(entry -> paths.set(entry.getKey(), entry.getValue()));
            }

            // Merge Schemas
            JsonNode serviceComponents = node.get("components");
            if (serviceComponents != null && serviceComponents.has("schemas")) {
                serviceComponents.get("schemas").fields().forEachRemaining(entry -> schemas.set(entry.getKey(), entry.getValue()));
            }
        }

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(master);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to generate aggregated JSON\"}";
        }
    }
}
