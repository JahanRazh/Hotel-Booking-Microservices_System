package com.hotel.payment.controller;

import com.hotel.payment.dto.PaymentDto;
import com.hotel.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "Manage hotel payments and refunds")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Process payment", description = "[ADMIN/USER] Process payment for a booking")
    public ResponseEntity<PaymentDto.Response> processPayment(@Valid @RequestBody PaymentDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.processPayment(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "[ADMIN/USER]")
    public ResponseEntity<PaymentDto.Response> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get payment by reference", description = "[ADMIN/USER]")
    public ResponseEntity<PaymentDto.Response> getPaymentByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(reference));
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "[ADMIN ONLY]")
    public ResponseEntity<List<PaymentDto.Response>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get payments by booking ID", description = "[ADMIN/USER]")
    public ResponseEntity<List<PaymentDto.Response>> getPaymentsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBooking(bookingId));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get payments by customer ID", description = "[ADMIN/USER]")
    public ResponseEntity<List<PaymentDto.Response>> getPaymentsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "[ADMIN/USER] Refund a completed payment")
    public ResponseEntity<PaymentDto.Response> refundPayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentDto.RefundRequest request) {
        return ResponseEntity.ok(paymentService.refundPayment(id, request));
    }
}
