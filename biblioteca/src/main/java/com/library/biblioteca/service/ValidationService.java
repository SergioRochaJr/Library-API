package com.library.biblioteca.service;

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
        if (customer.getLastname() == null || customer.getLastname().isEmpty()) {
            throw new IllegalArgumentException("O sobrenome do cliente não pode ser vazio");
        }
        if (customer.getAddress() == null || customer.getAddress().isEmpty()) {
            throw new IllegalArgumentException("O endereço do cliente não pode ser vazio");
        }
        if (customer.getCity() == null || customer.getCity().isEmpty()) {
            throw new IllegalArgumentException("A cidade do cliente não pode ser vazia");
        }
        if (customer.getState() == null) {
            throw new IllegalArgumentException("O estado do cliente não pode ser vazio");
        }
        if (customer.getCountry() == null || customer.getCountry().isEmpty()) {
            throw new IllegalArgumentException("O país do cliente não pode ser vazio");
        }
        if (customer.getBirthDate() == null) {
            throw new IllegalArgumentException("A data de nascimento do cliente não pode ser vazia");
        }
    }

    public void validateLoan(Loan loan) {
        if (loan.getCustomer() == null) {
            throw new IllegalArgumentException("O cliente do empréstimo não pode ser nulo");
        }
        if (loan.getBooks() == null || loan.getBooks().isEmpty()) {
            throw new IllegalArgumentException("A lista de livros não pode ser vazia");
        }
        if (loan.getLoanDate() == null) {
            throw new IllegalArgumentException("A data do empréstimo não pode ser vazia");
        }
    }
}
