package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingDto;
import com.hotel.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking API", description = "Manage hotel room bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create booking", description = "[ADMIN/USER] Create a new room booking (validates customer and room availability)")
    public ResponseEntity<BookingDto.Response> createBooking(@Valid @RequestBody BookingDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "[ADMIN/USER]")
    public ResponseEntity<BookingDto.Response> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get booking by reference", description = "[ADMIN/USER]")
    public ResponseEntity<BookingDto.Response> getBookingByReference(@PathVariable String reference) {
        return ResponseEntity.ok(bookingService.getBookingByReference(reference));
    }

    @GetMapping
    @Operation(summary = "Get all bookings", description = "[ADMIN ONLY]")
    public ResponseEntity<List<BookingDto.Response>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get bookings by customer ID", description = "[ADMIN/USER]")
    public ResponseEntity<List<BookingDto.Response>> getBookingsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(bookingService.getBookingsByCustomer(customerId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking", description = "[ADMIN/USER] Update booking parameters")
    public ResponseEntity<BookingDto.Response> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingDto.UpdateRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @DeleteMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "[ADMIN/USER] Cancel a booking and free up the room")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}
