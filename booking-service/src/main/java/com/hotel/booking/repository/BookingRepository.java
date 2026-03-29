package com.hotel.booking.repository;

import com.hotel.booking.model.Booking;
import com.hotel.booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByRoomIdAndStatusIn(Long roomId, List<BookingStatus> statuses);
    boolean existsByRoomIdAndStatusInAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Long roomId, List<BookingStatus> statuses, LocalDate checkOut, LocalDate checkIn);
}
