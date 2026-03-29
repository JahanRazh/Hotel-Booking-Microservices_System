package com.hotel.booking.service;

import com.hotel.booking.client.CustomerClient;
import com.hotel.booking.client.RoomClient;
import com.hotel.booking.dto.BookingDto;
import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingStatus;
import com.hotel.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomClient roomClient;
    private final CustomerClient customerClient;

    public BookingDto.Response createBooking(BookingDto.CreateRequest request) {
        // Validate dates
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        // Validate customer exists
        CustomerClient.CustomerResponse customer = customerClient.getCustomerById(request.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + request.getCustomerId());
        }

        // Validate room exists and is available
        RoomClient.RoomResponse room = roomClient.getRoomById(request.getRoomId());
        if (room == null) {
            throw new RuntimeException("Room not found with id: " + request.getRoomId());
        }
        if (!"AVAILABLE".equals(room.getStatus())) {
            throw new RuntimeException("Room " + room.getRoomNumber() + " is not available");
        }

        // Check room capacity
        if (request.getNumberOfGuests() > room.getCapacity()) {
            throw new RuntimeException("Number of guests exceeds room capacity of " + room.getCapacity());
        }

        // Check for overlapping bookings
        boolean hasOverlap = bookingRepository
                .existsByRoomIdAndStatusInAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                        request.getRoomId(),
                        List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN),
                        request.getCheckOutDate(),
                        request.getCheckInDate()
                );
        if (hasOverlap) {
            throw new RuntimeException("Room is already booked for the selected dates");
        }

        // Calculate total
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal totalAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .bookingReference(generateReference())
                .customerId(request.getCustomerId())
                .roomId(request.getRoomId())
                .roomNumber(room.getRoomNumber())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .numberOfGuests(request.getNumberOfGuests())
                .pricePerNight(room.getPricePerNight())
                .totalAmount(totalAmount)
                .specialRequests(request.getSpecialRequests())
                .build();

        // Update room status to RESERVED
        roomClient.updateRoomStatus(request.getRoomId(), "RESERVED");

        return toResponse(bookingRepository.save(booking));
    }

    public BookingDto.Response getBookingById(Long id) {
        return toResponse(bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id)));
    }

    public BookingDto.Response getBookingByReference(String reference) {
        return toResponse(bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found with reference: " + reference)));
    }

    public List<BookingDto.Response> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<BookingDto.Response> getBookingsByCustomer(Long customerId) {
        return bookingRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public BookingDto.Response updateBooking(Long id, BookingDto.UpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot update a cancelled booking");
        }

        if (request.getCheckInDate() != null) booking.setCheckInDate(request.getCheckInDate());
        if (request.getCheckOutDate() != null) booking.setCheckOutDate(request.getCheckOutDate());
        if (request.getNumberOfGuests() != null) booking.setNumberOfGuests(request.getNumberOfGuests());
        if (request.getSpecialRequests() != null) booking.setSpecialRequests(request.getSpecialRequests());
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
            // Free up room if cancelled or checked out
            if (request.getStatus() == BookingStatus.CANCELLED || request.getStatus() == BookingStatus.CHECKED_OUT) {
                roomClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
            }
            if (request.getStatus() == BookingStatus.CHECKED_IN) {
                roomClient.updateRoomStatus(booking.getRoomId(), "OCCUPIED");
            }
        }

        // Recalculate total if dates changed
        long nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        booking.setTotalAmount(booking.getPricePerNight().multiply(BigDecimal.valueOf(nights)));

        return toResponse(bookingRepository.save(booking));
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        roomClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
        bookingRepository.save(booking);
    }

    private String generateReference() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingDto.Response toResponse(Booking b) {
        return BookingDto.Response.builder()
                .id(b.getId())
                .bookingReference(b.getBookingReference())
                .customerId(b.getCustomerId())
                .roomId(b.getRoomId())
                .roomNumber(b.getRoomNumber())
                .checkInDate(b.getCheckInDate())
                .checkOutDate(b.getCheckOutDate())
                .numberOfGuests(b.getNumberOfGuests())
                .pricePerNight(b.getPricePerNight())
                .totalAmount(b.getTotalAmount())
                .status(b.getStatus())
                .specialRequests(b.getSpecialRequests())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
