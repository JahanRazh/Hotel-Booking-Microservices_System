package com.hotel.room.service;

import com.hotel.room.dto.RoomDto;
import com.hotel.room.model.Room;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.RoomType;
import com.hotel.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomDto.Response createRoom(RoomDto.CreateRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new RuntimeException("Room number already exists: " + request.getRoomNumber());
        }
        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .pricePerNight(request.getPricePerNight())
                .capacity(request.getCapacity())
                .description(request.getDescription())
                .amenities(request.getAmenities())
                .floorNumber(request.getFloorNumber())
                .build();
        return toResponse(roomRepository.save(room));
    }

    public RoomDto.Response getRoomById(Long id) {
        return toResponse(roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id)));
    }

    public RoomDto.Response getRoomByNumber(String roomNumber) {
        return toResponse(roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomNumber)));
    }

    public List<RoomDto.Response> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomDto.Response> getAvailableRooms() {
        return roomRepository.findByStatus(RoomStatus.AVAILABLE).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomDto.Response> getRoomsByType(RoomType roomType) {
        return roomRepository.findByRoomType(roomType).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public RoomDto.Response updateRoom(Long id, RoomDto.UpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        if (request.getRoomType() != null) room.setRoomType(request.getRoomType());
        if (request.getPricePerNight() != null) room.setPricePerNight(request.getPricePerNight());
        if (request.getCapacity() != null) room.setCapacity(request.getCapacity());
        if (request.getDescription() != null) room.setDescription(request.getDescription());
        if (request.getAmenities() != null) room.setAmenities(request.getAmenities());
        if (request.getFloorNumber() != null) room.setFloorNumber(request.getFloorNumber());
        if (request.getStatus() != null) room.setStatus(request.getStatus());
        return toResponse(roomRepository.save(room));
    }

    public RoomDto.Response updateRoomStatus(Long id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        room.setStatus(status);
        return toResponse(roomRepository.save(room));
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
        roomRepository.delete(room);
    }

    private RoomDto.Response toResponse(Room r) {
        return RoomDto.Response.builder()
                .id(r.getId())
                .roomNumber(r.getRoomNumber())
                .roomType(r.getRoomType())
                .pricePerNight(r.getPricePerNight())
                .capacity(r.getCapacity())
                .description(r.getDescription())
                .amenities(r.getAmenities())
                .floorNumber(r.getFloorNumber())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
