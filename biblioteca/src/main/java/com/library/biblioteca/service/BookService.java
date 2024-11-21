package com.library.biblioteca.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.dto.BookDTO;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.model.LoanStatus;
import com.library.biblioteca.repository.BookRepository;
import com.library.biblioteca.repository.LoanRepository;
import com.library.biblioteca.util.BookMapper;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired

    private LoanRepository loanRepository;

    @Autowired
    private ValidationService validationService;  // Serviço de validação centralizado


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

    public boolean delete(Long id) {
        // Verificar se o livro existe
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
    
            // Buscar todos os empréstimos que possuem o livro no relacionamento
            List<Loan> loans = loanRepository.findBookById(id);
    
            // Verificar se todos os empréstimos possuem status RETURNED
            boolean allLoansReturned = loans.stream()
                    .allMatch(loan -> loan.getStatus() == LoanStatus.RETURNED);
    
            // Verificar se o status do livro não é BORROWED
            boolean bookIsNotBorrowed = book.getStatus() != BookStatus.BORROWED;
    
            // Se todos os empréstimos estiverem com status RETURNED e o livro não estiver com status BORROWED, excluir o livro
            if (allLoansReturned && bookIsNotBorrowed) {
                bookRepository.deleteById(id);
                return true;  // Livro excluído com sucesso
            }
        }
        // Caso o livro tenha empréstimos não retornados ou o status do livro seja BORROWED
        return false;
    }
    

}
