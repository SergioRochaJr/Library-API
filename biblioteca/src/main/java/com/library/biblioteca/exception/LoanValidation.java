package com.library.biblioteca.exception;

import java.time.LocalDate;

import com.library.biblioteca.model.Loan;

public class LoanValidation {

    public static void validate(Loan loan) {
        if (loan.getLoanDate() == null) {
            throw new ValidationException("A data do empréstimo é obrigatória.");
        } else if (loan.getLoanDate().isAfter(LocalDate.now())) {
            throw new ValidationException("A data do empréstimo não pode ser no futuro.");
        }

        if (loan.getCustomer() == null) {
            throw new ValidationException("O cliente do empréstimo é obrigatório.");
        }

        if (loan.getBooks() == null || loan.getBooks().isEmpty()) {
            throw new ValidationException("É necessário associar pelo menos um livro ao empréstimo.");
        }
    }
}
