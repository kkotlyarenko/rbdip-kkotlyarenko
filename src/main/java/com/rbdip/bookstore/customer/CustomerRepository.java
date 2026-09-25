package com.rbdip.bookstore.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByFullNameAndPhone(String fullName, String phone);

    Optional<Customer> findByFullNameAndPhoneIsNull(String fullName);
}
