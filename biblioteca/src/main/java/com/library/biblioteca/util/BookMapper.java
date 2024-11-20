package com.library.biblioteca.util;

import com.library.biblioteca.dto.BookDTO;
import com.library.biblioteca.model.Book;

public class BookMapper {

    // Converte a entidade Book para DTO
    public static BookDTO toDTO(Book book) {
        if (book == null) {
            return null;
        }
        return new BookDTO(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getIsbn(),
            book.getPublishedDate(),
            book.getStatus()
        );
    }

    // Converte o DTO BookDTO para entidade Book
    public static Book toEntity(BookDTO bookDTO) {
        if (bookDTO == null) {
            return null;
        }
        return new Book(
            bookDTO.getId(),
            bookDTO.getTitle(),
            bookDTO.getAuthor(),
            bookDTO.getIsbn(),
            bookDTO.getPublishedDate(),
            bookDTO.getStatus()
        );
    }
}
