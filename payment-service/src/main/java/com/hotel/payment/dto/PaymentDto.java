package com.hotel.payment.dto;

import com.hotel.payment.model.PaymentMethod;
import com.hotel.payment.model.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    @Data
    public static class CreateRequest {
        @NotNull(message = "Booking ID is required")
        private Long bookingId;

        @NotNull(message = "Payment method is required")
        private PaymentMethod paymentMethod;
    }

    @Data
    public static class RefundRequest {
        @NotBlank(message = "Reason is required")
        private String reason;
    }

    @Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    @lombok.Builder
    public static class Response {
        private Long id;
        private String paymentReference;
        private Long bookingId;
        private String bookingReference;
        private Long customerId;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private PaymentStatus status;
        private String transactionId;
        private String failureReason;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
