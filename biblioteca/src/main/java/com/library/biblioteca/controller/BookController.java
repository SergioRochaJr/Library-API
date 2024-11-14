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
        }
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
            BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
            if (bookService.updateStatus(id, bookStatus)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Status inválido: " + status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            if (bookService.delete(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro ao deletar livro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
