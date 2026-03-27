package com.hotel.payment.service;

import com.hotel.payment.client.BookingClient;
import com.hotel.payment.dto.PaymentDto;
import com.hotel.payment.model.Payment;
import com.hotel.payment.model.PaymentStatus;
import com.hotel.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingClient bookingClient;

    public PaymentDto.Response processPayment(PaymentDto.CreateRequest request) {
        // Fetch booking details
        BookingClient.BookingResponse booking = bookingClient.getBookingById(request.getBookingId());
        if (booking == null) {
            throw new RuntimeException("Booking not found with id: " + request.getBookingId());
        }

        // Ensure booking is CONFIRMED and not already paid
        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Cannot process payment for a cancelled booking");
        }

        paymentRepository.findByBookingIdAndStatus(request.getBookingId(), PaymentStatus.COMPLETED)
                .ifPresent(p -> { throw new RuntimeException("Booking already has a completed payment: " + p.getPaymentReference()); });

        // Simulate payment processing
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        boolean paymentSuccess = simulatePaymentGateway(request.getPaymentMethod().name());

        Payment payment = Payment.builder()
                .paymentReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .customerId(booking.getCustomerId())
                .amount(booking.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionId(transactionId)
                .status(paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .failureReason(paymentSuccess ? null : "Payment gateway declined the transaction")
                .paidAt(paymentSuccess ? LocalDateTime.now() : null)
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    public PaymentDto.Response getPaymentById(Long id) {
        return toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id)));
    }

    public PaymentDto.Response getPaymentByReference(String reference) {
        return toResponse(paymentRepository.findByPaymentReference(reference)
                .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + reference)));
    }

    public List<PaymentDto.Response> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PaymentDto.Response> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<PaymentDto.Response> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public PaymentDto.Response refundPayment(Long id, PaymentDto.RefundRequest request) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setFailureReason("Refund reason: " + request.getReason());
        return toResponse(paymentRepository.save(payment));
    }

    // Simulates a payment gateway: always succeeds for demo
    private boolean simulatePaymentGateway(String method) {
        return true;
    }

    private PaymentDto.Response toResponse(Payment p) {
        return PaymentDto.Response.builder()
                .id(p.getId())
                .paymentReference(p.getPaymentReference())
                .bookingId(p.getBookingId())
                .bookingReference(p.getBookingReference())
                .customerId(p.getCustomerId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .failureReason(p.getFailureReason())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
