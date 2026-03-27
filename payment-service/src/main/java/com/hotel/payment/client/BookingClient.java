package com.hotel.payment.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BookingClient {

    @Value("${service.booking.url}")
    private String bookingServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public BookingResponse getBookingById(Long bookingId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(bookingServiceUrl + "/api/bookings/" + bookingId)
                    .retrieve()
                    .bodyToMono(BookingResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch booking with id: " + bookingId + ". Error: " + e.getMessage());
        }
    }

    @Data
    public static class BookingResponse {
        private Long id;
        private String bookingReference;
        private Long customerId;
        private Long roomId;
        private String roomNumber;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private BigDecimal totalAmount;
        private String status;
    }
}
