package com.library.biblioteca.service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.library.biblioteca.exception.ValidationException;
import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.Loan;

@Service
public class ValidationService {

    // Validações para o livro
    public void validateBook(Book book) {
        validateIsbn(book.getIsbn());
        validateAuthor(book.getAuthor());
        validateTitle(book.getTitle());
    }

    // Valida o formato do ISBN (978-0261103573)
    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.isEmpty()) {
            throw new ValidationException("O ISBN é obrigatório.");
        }
     
        // Expressão regular para o formato do ISBN: 978-0261103573
        String isbnPattern = "\\d{3}-\\d{10}"; 
        Pattern pattern = Pattern.compile(isbnPattern);
        Matcher matcher = pattern.matcher(isbn);

        if (!matcher.matches()) {
            throw new ValidationException("O ISBN deve estar no formato: xxx-xxxxxxxxxx (3 dígitos, hífen, 10 dígitos).");
        }
    }

    // Valida o nome do autor (sem caracteres especiais, mas permite hífens)
    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new ValidationException("O título do livro é obrigatório.");
        }
    }
    private void validateAuthor(String author) {
        if (author == null || author.isEmpty()) {
            throw new ValidationException("O autor é obrigatório.");
        }

        // Expressão regular para permitir letras, espaços e hífens, mas não permite caracteres especiais
        String authorPattern = "^[A-Za-zÀ-ÿ0-9\\s-.]+$";  // Letras, números, espaços e hífens
        Pattern pattern = Pattern.compile(authorPattern);
        Matcher matcher = pattern.matcher(author);

        if (!matcher.matches()) {
            throw new ValidationException("O nome do autor não pode conter caracteres especiais.");
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


}
