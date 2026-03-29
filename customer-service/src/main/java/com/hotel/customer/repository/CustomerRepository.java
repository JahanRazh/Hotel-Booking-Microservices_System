package com.hotel.customer.repository;

import com.hotel.customer.model.Customer;
import com.hotel.customer.model.CustomerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNationalId(String nationalId);
    List<Customer> findByStatus(CustomerStatus status);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId);
}
