package com.hotel.customer.dto;

import com.hotel.customer.model.CustomerStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

public class CustomerDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Phone is required")
        private String phone;

        private String address;
        private String city;
        private String country;
        private String nationalId;
    }

    @Data
    public static class UpdateRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private String city;
        private String country;
        private CustomerStatus status;
    }

    @Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class Response {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String country;
        private String nationalId;
        private CustomerStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
