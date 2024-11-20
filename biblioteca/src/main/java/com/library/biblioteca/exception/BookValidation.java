package com.library.biblioteca.exception;

import com.library.biblioteca.model.Book;

public class BookValidation {

    public static void validate(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new ValidationException("O título do livro é obrigatório.");
        }
        
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new ValidationException("O autor do livro é obrigatório.");
        }

        if (book.getStatus() == null) {
            throw new ValidationException("O status do livro é obrigatório.");
        }
    }
}
