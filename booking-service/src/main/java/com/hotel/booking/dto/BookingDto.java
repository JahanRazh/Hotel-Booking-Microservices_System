package com.hotel.booking.dto;

import com.hotel.booking.model.BookingStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookingDto {

    @Data
    public static class CreateRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotNull(message = "Room ID is required")
        private Long roomId;

        @NotNull(message = "Check-in date is required")
        @FutureOrPresent(message = "Check-in date must be today or in the future")
        private LocalDate checkInDate;

        @NotNull(message = "Check-out date is required")
        @Future(message = "Check-out date must be in the future")
        private LocalDate checkOutDate;

        @NotNull(message = "Number of guests is required")
        @Min(value = 1)
        private Integer numberOfGuests;

        private String specialRequests;
    }

    @Data
    public static class UpdateRequest {
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Integer numberOfGuests;
        private String specialRequests;
        private BookingStatus status;
    }

    @Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class Response {
        private Long id;
        private String bookingReference;
        private Long customerId;
        private Long roomId;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Integer numberOfGuests;
        private BigDecimal pricePerNight;
        private BigDecimal totalAmount;
        private BookingStatus status;
        private String specialRequests;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
