package com.rbdip.bookstore.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByFirstNameAndLastNameAndPhone(String firstName, String lastName, String phone);

    Optional<Customer> findByFirstNameAndLastNameAndPhoneIsNull(String firstName, String lastName);
}
