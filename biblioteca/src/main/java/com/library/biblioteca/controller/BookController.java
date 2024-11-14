package com.library.biblioteca.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.service.BookService;
import com.library.biblioteca.service.ValidationService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    public ResponseEntity<List<Book>> getAll(@RequestParam(required = false) Long id) {
        if (id != null) {
            Book book = bookService.findById(id);
            if (book != null) {
                return ResponseEntity.ok(List.of(book));
            }
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(bookService.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable("id") Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Book book) {
        try {
            if (book.getStatus() != null) {
                try {
                    String statusStr = book.getStatus().toString();
                    if (!isValidStatus(statusStr)) {
                        ErrorResponse errorResponse = new ErrorResponse("Status inválido. Os valores válidos são: AVAILABLE, BORROWED");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                    }
                } catch (Exception e) {
                    ErrorResponse errorResponse = new ErrorResponse("Erro ao processar o status: " + e.getMessage());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            } else {
                ErrorResponse errorResponse = new ErrorResponse("O status do livro é obrigatório.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
    
            validationService.validateBook(book);

            bookService.create(book);
    
            URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(book.getId())
                            .toUri();
            return ResponseEntity.created(location).body(book);
    
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    private boolean isValidStatus(String status) {
        return "AVAILABLE".equals(status) || "BORROWED".equals(status);
    }
    
    
    
    @PutMapping
    public ResponseEntity<?> update(@RequestBody Book book) {
        try {
            validationService.validateBook(book);
            if (bookService.update(book)) {
                return ResponseEntity.ok(book);
            }
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        try {
            // Tenta atualizar o status
            BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
            if (bookService.updateStatus(id, bookStatus)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
    
        } catch (IllegalArgumentException e) {
            // Adiciona a informação sobre os status válidos
            String validStatuses = "AVAILABLE, BORROWED";
            ErrorResponse errorResponse = new ErrorResponse("Status inválido: " + status + ". Status válidos: " + validStatuses);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            // Tenta excluir o livro
            if (bookService.delete(id)) {
                // Se o livro for deletado com sucesso, retorna 204 No Content
                return ResponseEntity.ok("Livro com ID " + id + " foi deletado com sucesso.");
            } else {
                // Se não encontrar o livro para deletar, retorna 404 Not Found
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                     .body("Livro com ID " + id + " não encontrado.");
            }
        } catch (IllegalArgumentException e) {
            // Em caso de erro, retorna 400 Bad Request com a mensagem de erro
            ErrorResponse errorResponse = new ErrorResponse("Erro ao deletar livro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado e retorna 500 Internal Server Error
            ErrorResponse errorResponse = new ErrorResponse("Erro inesperado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
}
