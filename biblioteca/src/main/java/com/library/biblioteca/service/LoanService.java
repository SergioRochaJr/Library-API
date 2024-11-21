package com.library.biblioteca.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.exception.LoanValidation;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.model.LoanStatus;
import com.library.biblioteca.repository.LoanRepository;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    public Loan findByBookId(Long book_id) {
        return loanRepository.findById(book_id).orElse(null);
    }

    

    public List<Loan> findByCustomerId(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<Loan> findByLoanDateBetween(LocalDate startDate, LocalDate endDate) {
        return loanRepository.findByLoanDateBetween(startDate, endDate);
    }

    public Loan create(Loan loan) {
        LoanValidation.validate(loan);
        loan.setStatus(LoanStatus.ACTIVE);
        return loanRepository.save(loan);
    }

    public boolean update(Loan loan) {
        if (loanRepository.existsById(loan.getId())) {
            LoanValidation.validate(loan);
            loanRepository.save(loan);
            return true;
        }
        return false;
    }

    public boolean delete(Long id) {
        if (loanRepository.existsById(id)) {
            loanRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
