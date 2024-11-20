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

import com.library.biblioteca.dto.BookDTO;
import com.library.biblioteca.model.BookStatus;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.service.BookService;
import com.library.biblioteca.service.ValidationService;
import com.library.biblioteca.util.BookMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoints para gerenciamento de livros")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    @Operation(summary = "Listar livros", description = "Retorna todos os livros ou um específico, baseado no ID")
    @ApiResponse(responseCode = "200", description = "Lista de livros retornada com sucesso")
    public ResponseEntity<List<BookDTO>> getAll(@RequestParam(required = false) Long id) {
        if (id != null) {
            BookDTO bookDTO = bookService.findById(id);
            if (bookDTO != null) {
                return ResponseEntity.ok(List.of(bookDTO));
            }
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(bookService.findAll());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter livro por ID", description = "Busca os detalhes de um livro pelo ID")
    @ApiResponse(responseCode = "200", description = "Livro encontrado")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    public ResponseEntity<BookDTO> getById(@PathVariable("id") Long id) {
        BookDTO bookDTO = bookService.findById(id);
        if (bookDTO != null) {
            return ResponseEntity.ok(bookDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Criar livro", description = "Adiciona um novo livro ao sistema")
    @ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    public ResponseEntity<?> create(@RequestBody BookDTO bookDTO) {
        try {
            validationService.validateBook(BookMapper.toEntity(bookDTO));
            bookService.create(bookDTO);
            URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(bookDTO.getId())
                            .toUri();
            return ResponseEntity.created(location).body(bookDTO);

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping
    @Operation(summary = "Atualizar livro", description = "Atualiza as informações de um livro existente")
    @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    public ResponseEntity<?> update(@RequestBody BookDTO bookDTO) {
        try {
            validationService.validateBook(BookMapper.toEntity(bookDTO));
            if (bookService.update(bookDTO)) {
                return ResponseEntity.ok(bookDTO);
            }
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do livro", description = "Modifica o status de disponibilidade do livro")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido")
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
    @Operation(summary = "Excluir livro", description = "Remove um livro do sistema")
    @ApiResponse(responseCode = "204", description = "Livro excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Livro não encontrado")
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
