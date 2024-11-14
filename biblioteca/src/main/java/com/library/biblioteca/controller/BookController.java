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
import com.library.biblioteca.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // GET: Retorna todos os livros ou um livro pelo ID
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

    // GET: Retorna um livro específico pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getById(@PathVariable("id") Long id) {
        Book book = bookService.findById(id);
        if (book != null) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    // POST: Cria um novo livro
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        try {
            bookService.create(book);
            URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(book.getId())
                                .toUri();
            return ResponseEntity.created(location).body(book);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // PUT: Atualiza os dados de um livro (exceto o status)
    @PutMapping
    public ResponseEntity<Book> update(@RequestBody Book book) {
        try {
            if (bookService.update(book)) {
                return ResponseEntity.ok(book);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // PATCH: Atualiza o status do livro
    @PatchMapping("/{id}/status")
    public ResponseEntity<Book> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        try {
            // Verificando se o status é válido e convertendo para o tipo enum BookStatus
            BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());  // Converte o status para o enum
            if (bookService.updateStatus(id, bookStatus)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Caso a conversão do status falhe, retorna um erro de BAD_REQUEST
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // DELETE: Inativa o livro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        try {
            if (bookService.delete(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
