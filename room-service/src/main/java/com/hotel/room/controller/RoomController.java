package com.hotel.room.controller;

import com.hotel.room.dto.RoomDto;
import com.hotel.room.model.RoomStatus;
import com.hotel.room.model.RoomType;
import com.hotel.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room API", description = "Manage hotel rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "Create room", description = "[ADMIN ONLY] Add a new room to the hotel")
    public ResponseEntity<RoomDto.Response> createRoom(@Valid @RequestBody RoomDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createRoom(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID", description = "[ADMIN/USER]")
    public ResponseEntity<RoomDto.Response> getRoom(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/number/{roomNumber}")
    @Operation(summary = "Get room by room number", description = "[ADMIN/USER]")
    public ResponseEntity<RoomDto.Response> getRoomByNumber(@PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomByNumber(roomNumber));
    }

    @GetMapping
    @Operation(summary = "Get all rooms", description = "[ADMIN/USER]")
    public ResponseEntity<List<RoomDto.Response>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available rooms", description = "[ADMIN/USER]")
    public ResponseEntity<List<RoomDto.Response>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/type/{roomType}")
    @Operation(summary = "Get rooms by type", description = "[ADMIN/USER]")
    public ResponseEntity<List<RoomDto.Response>> getRoomsByType(@PathVariable RoomType roomType) {
        return ResponseEntity.ok(roomService.getRoomsByType(roomType));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update room", description = "[ADMIN ONLY] Update an existing room")
    public ResponseEntity<RoomDto.Response> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomDto.UpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update room status", description = "[ADMIN ONLY] Update the status of a specific room")
    public ResponseEntity<RoomDto.Response> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room", description = "[ADMIN ONLY] Remove a room permanently")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
