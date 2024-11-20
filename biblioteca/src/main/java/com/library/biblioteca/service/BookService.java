package com.library.biblioteca.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.dto.BookDTO;
import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.repository.BookRepository;
import com.library.biblioteca.util.BookMapper;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ValidationService validationService;

    public void create(BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);
        validationService.validateBook(book);
        bookRepository.save(book);
    }

    public boolean update(BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);
        validationService.validateBook(book);
        
        if (bookRepository.existsById(book.getId())) {
            Optional<Book> existingBookOpt = bookRepository.findById(book.getId());
            if (existingBookOpt.isPresent()) {
                Book existingBook = existingBookOpt.get();
                book.setStatus(existingBook.getStatus());
                bookRepository.save(book);
                return true;
            }
        }
        return false;
    }

    public boolean updateStatus(Long id, BookStatus status) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();
            book.setStatus(status);
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    // Buscar livro por ID (agora retorna um BookDTO)
    public BookDTO findById(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        return BookMapper.toDTO(book);  // Convertendo a entidade para DTO
    }

    // Listar todos os livros (agora retorna uma lista de BookDTO)
    public List<BookDTO> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookMapper::toDTO)  // Convertendo para DTO
                .collect(Collectors.toList());
    }

    // Deletar livro (permanece o mesmo, pois não altera a lógica de DTO)
    public boolean delete(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
