package com.library.biblioteca.service;

import com.library.biblioteca.dto.BookDTO;
import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.repository.BookRepository;
import com.library.biblioteca.util.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ValidationService validationService;  // Serviço de validação centralizado

    // Criar livro (agora recebe um BookDTO)
    public void create(BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);  // Convertendo para a entidade Book
        validationService.validateBook(book);  // Validação centralizada
        bookRepository.save(book);
    }

    // Atualizar livro (agora recebe um BookDTO)
    public boolean update(BookDTO bookDTO) {
        Book book = BookMapper.toEntity(bookDTO);  // Convertendo para a entidade Book
        validationService.validateBook(book);  // Validação centralizada
        if (bookRepository.existsById(book.getId())) {
            bookRepository.save(book);
            return true;
        }
        return false;
    }

    // Atualizar o status de um livro
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
