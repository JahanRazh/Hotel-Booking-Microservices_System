package com.hotel.booking.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class CustomerClient {

    @Value("${service.customer.url}")
    private String customerServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public CustomerResponse getCustomerById(Long customerId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(customerServiceUrl + "/api/customers/" + customerId)
                    .retrieve()
                    .bodyToMono(CustomerResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch customer with id: " + customerId + ". Error: " + e.getMessage());
        }
    }

    @Data
    public static class CustomerResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String status;
    }
}
