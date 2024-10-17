package com.library.biblioteca.controller;


import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.service.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    // GET: Retorna todos os livros ou um livro pelo ID
    @GetMapping
    public ResponseEntity<List<Book>> getAll(@RequestParam(required = false) Long id){
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
        bookService.create(book);
        URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(book.getId())
                            .toUri();
        return ResponseEntity.created(location).body(book);
    }

    // PUT: Atualiza os dados de um livro (exceto o status)
    @PutMapping
    public ResponseEntity<Book> update(@RequestBody Book book) {
        if (bookService.update(book)) {
            return ResponseEntity.ok(book);
        }
        return ResponseEntity.notFound().build();
    }

    // PATCH: Atualiza o status do livro
    @PatchMapping("/{id}/status")
    public ResponseEntity<Book> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        if (bookService.updateStatus(id, status)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE: Inativa o livro
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (bookService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
