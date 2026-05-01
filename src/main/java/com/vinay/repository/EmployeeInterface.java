package com.vinay.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vinay.entity.Employee;

@Repository
public interface EmployeeInterface extends JpaRepository<Employee, Long> {

	Optional<Employee> findByEmail(String email);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String firstName,
        String lastName,
        String email,
        Pageable pageable
    );

}
