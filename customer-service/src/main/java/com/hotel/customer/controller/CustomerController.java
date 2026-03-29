package com.hotel.customer.controller;

import com.hotel.customer.dto.CustomerDto;
import com.hotel.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer API", description = "Manage hotel customers")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create customer", description = "[ADMIN/USER] Register a new customer")
    public ResponseEntity<CustomerDto.Response> createCustomer(
            @Valid @RequestBody CustomerDto.CreateRequest request,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "ROLE_SYSTEM") String role,
            @RequestHeader(value = "X-Auth-Email", defaultValue = "") String email) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request, role, email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "[ADMIN/USER]")
    public ResponseEntity<CustomerDto.Response> getCustomer(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "ROLE_SYSTEM") String role,
            @RequestHeader(value = "X-Auth-Email", defaultValue = "") String email) {
        return ResponseEntity.ok(customerService.getCustomerById(id, role, email));
    }

    @GetMapping
    @Operation(summary = "Get all customers", description = "[ADMIN ONLY]")
    public ResponseEntity<List<CustomerDto.Response>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "[ADMIN/USER] Update existing customer details")
    public ResponseEntity<CustomerDto.Response> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerDto.UpdateRequest request,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "ROLE_SYSTEM") String role,
            @RequestHeader(value = "X-Auth-Email", defaultValue = "") String email) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request, role, email));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "[ADMIN ONLY] Permanently delete a customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{emailPath}")
    @Operation(summary = "Get customer by email", description = "[ADMIN/USER]")
    public ResponseEntity<CustomerDto.Response> getCustomerByEmail(
            @PathVariable("emailPath") String emailPath,
            @RequestHeader(value = "X-Auth-Role", defaultValue = "ROLE_SYSTEM") String role,
            @RequestHeader(value = "X-Auth-Email", defaultValue = "") String authEmail) {
        return ResponseEntity.ok(customerService.getCustomerByEmail(emailPath, role, authEmail));
    }
}
