package com.library.biblioteca.service;

import java.time.LocalDate;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.Loan;

@Service
public class ValidationService {

    public void validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new IllegalArgumentException("O título do livro não pode ser vazio");
        }
        if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
            throw new IllegalArgumentException("O autor do livro não pode ser vazio");
        }
        if (book.getIsbn() == null || book.getIsbn().isEmpty()) {
            throw new IllegalArgumentException("O ISBN do livro não pode ser vazio");
        }
        if (book.getPublishedDate() == null) {
            throw new IllegalArgumentException("A data de publicação do livro não pode ser vazia");
        }
    }

    public void validateCustomer(Customer customer) {
        if (customer.getName() == null || customer.getName().isEmpty()) {
            throw new IllegalArgumentException("O nome do cliente não pode ser vazio");
        }
        
        // Validação para garantir que o nome contenha apenas letras e espaços
        if (!customer.getName().matches("^[a-zA-ZÀ-ÿ\\s]+$")) {
            throw new IllegalArgumentException("Erro de validação: O nome do cliente contém caracteres inválidos");
        }
    
        if (customer.getLastname() == null || customer.getLastname().isEmpty()) {
            throw new IllegalArgumentException("O sobrenome do cliente não pode ser vazio");
        }
    
        if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
            throw new IllegalArgumentException("O endereço do cliente não pode ser vazio");
        }
    }

    public void validateLoan(Loan loan) {
        if (loan.getLoanDate() == null || loan.getLoanDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data do empréstimo é inválida.");
        }
        if (loan.getCustomer() == null) {
            throw new IllegalArgumentException("O empréstimo deve ter um cliente associado.");
        }
        if (loan.getBooks() == null || loan.getBooks().isEmpty()) {
            throw new IllegalArgumentException("O empréstimo deve ter ao menos um livro.");
        }
    }

    private boolean isValidName(String name) {
        String regex = "^[a-zA-Z\\s]+$";
        return Pattern.matches(regex, name);
    }

    private boolean isValidAddress(String address) {
        String regex = "^[a-zA-Z0-9,\\s]+$";
        return Pattern.matches(regex, address);
    }
}
