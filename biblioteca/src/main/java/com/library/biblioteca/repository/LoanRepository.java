package com.library.biblioteca.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.biblioteca.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long Id);
    List<Loan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate);
    List<Loan> findBookById(Long book_idLong);

}

