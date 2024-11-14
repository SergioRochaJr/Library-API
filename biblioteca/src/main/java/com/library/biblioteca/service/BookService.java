package com.library.biblioteca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.repository.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ValidationService validationService;

    public void create(Book book) {
        validationService.validateBook(book);
        bookRepository.save(book);
    }

    public boolean update(Book book) {
        validationService.validateBook(book);
        if (bookRepository.existsById(book.getId())) {
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    public boolean updateStatus(Long id, String status) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isPresent()) {
            try {
                BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());  // Converte para enum
                Book book = bookOpt.get();
                book.setStatus(bookStatus);
                bookRepository.save(book);
                return true;
            } catch (IllegalArgumentException e) {
                return false;  // Se o status for inválido, retorna false
            }
        }
        return false;
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public boolean delete(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
