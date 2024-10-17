package com.library.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.library.biblioteca.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByName(String name);

    List<Customer> findByBirthDate(LocalDate birthDate);

    boolean existsByIdAndLoansEmpty(Long id);
}
