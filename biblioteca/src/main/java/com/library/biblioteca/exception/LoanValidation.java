package com.library.biblioteca.exception;

import java.time.LocalDate;

import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.model.Loan;

public class LoanValidation {

    public static void validate(Loan loan) {
        if (loan.getLoanDate() == null) {
            throw new ValidationException("A data do empréstimo é obrigatória.");
        } else if (loan.getLoanDate().isAfter(LocalDate.now())) {
            throw new ValidationException("A data do empréstimo não pode ser no futuro.");
        }

        if (loan.getCustomer() == null || loan.getCustomer().getId() == null) {
            throw new ValidationException("O cliente é obrigatório.");
        }

        if (loan.getBooks().size() > 2) {
            throw new ValidationException("Não é permitido emprestar mais de dois livros.");
        }

        // Verifique se o status do livro é diferente de null antes de comparar
        if (loan.getBooks().stream().anyMatch(book -> book.getStatus() != null && book.getStatus().equals(BookStatus.BORROWED))) {
            throw new ValidationException("Existem livros já emprestados.");
        }
    }
}
