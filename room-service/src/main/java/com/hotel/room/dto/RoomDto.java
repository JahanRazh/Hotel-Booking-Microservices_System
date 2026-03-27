package com.hotel.room.dto;

import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.RoomType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RoomDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Room number is required")
        private String roomNumber;

        @NotNull(message = "Room type is required")
        private RoomType roomType;

        @NotNull(message = "Price per night is required")
        @DecimalMin(value = "0.0", inclusive = false)
        private BigDecimal pricePerNight;

        @NotNull(message = "Capacity is required")
        @Min(value = 1)
        private Integer capacity;

        private String description;
        private String amenities;
        private Integer floorNumber;
    }

    @Data
    public static class UpdateRequest {
        private RoomType roomType;
        private BigDecimal pricePerNight;
        private Integer capacity;
        private String description;
        private String amenities;
        private Integer floorNumber;
        private RoomStatus status;
    }

    @Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class Response {
        private Long id;
        private String roomNumber;
        private RoomType roomType;
        private BigDecimal pricePerNight;
        private Integer capacity;
        private String description;
        private String amenities;
        private Integer floorNumber;
        private RoomStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
