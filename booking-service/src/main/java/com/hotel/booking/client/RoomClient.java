package com.hotel.booking.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RoomClient {

    @Value("${service.room.url}")
    private String roomServiceUrl;

    private final WebClient.Builder webClientBuilder;

    public RoomResponse getRoomById(Long roomId) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(roomServiceUrl + "/api/rooms/" + roomId)
                    .retrieve()
                    .bodyToMono(RoomResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch room with id: " + roomId + ". Error: " + e.getMessage());
        }
    }

    public void updateRoomStatus(Long roomId, String status) {
        try {
            webClientBuilder.build()
                    .patch()
                    .uri(roomServiceUrl + "/api/rooms/" + roomId + "/status?status=" + status)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            // Log but don't fail — room status update is best-effort
            System.err.println("Warning: Could not update room status: " + e.getMessage());
        }
    }

    @Data
    public static class RoomResponse {
        private Long id;
        private String roomNumber;
        private String roomType;
        private BigDecimal pricePerNight;
        private Integer capacity;
        private String status;
        private String description;
        private String amenities;
    }
}
