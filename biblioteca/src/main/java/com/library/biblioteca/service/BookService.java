package com.library.biblioteca.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.exception.BookValidation;
import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.repository.BookRepository;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.orElse(null);
    }

    public void create(Book book) {
        BookValidation.validate(book); // Validação antes de salvar
        bookRepository.save(book);
    }

    public boolean update(Book book) {
        BookValidation.validate(book); // Validação antes de atualizar
        if (bookRepository.existsById(book.getId())) {
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    public boolean delete(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Método para atualizar o status do livro
    public boolean updateStatus(Long id, BookStatus status) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setStatus(status);  // Atualiza o status do livro
            bookRepository.save(book);  // Salva as alterações no banco de dados
            return true;
        }
        return false;  // Retorna false caso o livro não seja encontrado
    }
}
