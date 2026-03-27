package com.hotel.payment.repository;

import com.hotel.payment.model.Payment;
import com.hotel.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentReference(String paymentReference);
    List<Payment> findByBookingId(Long bookingId);
    List<Payment> findByCustomerId(Long customerId);
    List<Payment> findByStatus(PaymentStatus status);
    Optional<Payment> findByBookingIdAndStatus(Long bookingId, PaymentStatus status);
}
