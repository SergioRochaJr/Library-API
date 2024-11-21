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
import com.library.biblioteca.exception.SuccessResponse;
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
@ApiResponse(responseCode = "404", description = "Livro não encontrado")
public ResponseEntity<Object> getAll(@RequestParam(required = false) Long id) {
    if (id != null) {
        BookDTO bookDTO = bookService.findById(id);
        if (bookDTO != null) {

            SuccessResponse successResponse = new SuccessResponse("Livro encontrado com sucesso", List.of(bookDTO));
            return ResponseEntity.ok(successResponse);
        }

        ErrorResponse errorResponse = new ErrorResponse("Livro não encontrado com o ID " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    } else {
        List<BookDTO> books = bookService.findAll();
        if (books.isEmpty()) {

            ErrorResponse errorResponse = new ErrorResponse("Nenhum livro encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        SuccessResponse successResponse = new SuccessResponse("Lista de livros retornada com sucesso", books);
        return ResponseEntity.ok(successResponse);

    }
}



@PostMapping
@Operation(summary = "Criar livro", description = "Adiciona um novo livro ao sistema")
@ApiResponse(responseCode = "201", description = "Livro criado com sucesso")
@ApiResponse(responseCode = "400", description = "Erro de validação")
public ResponseEntity<Object> create(@RequestBody BookDTO bookDTO) {
    try {
        // Validando a entidade
        validationService.validateBook(BookMapper.toEntity(bookDTO));
        bookService.create(bookDTO);

        // Criando a URI do novo recurso
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(bookDTO.getId())
                        .toUri();

        // Criando a resposta de sucesso
        SuccessResponse successResponse = new SuccessResponse("Livro criado com sucesso", bookDTO);
        return ResponseEntity.created(location).body(successResponse);

    } catch (IllegalArgumentException e) {
        // Criando a resposta de erro
        ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


@PutMapping
@Operation(summary = "Atualizar livro", description = "Atualiza as informações de um livro existente")
@ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso")
@ApiResponse(responseCode = "404", description = "Livro não encontrado")
public ResponseEntity<Object> update(@RequestBody BookDTO bookDTO) {
    try {
        // Validando a entidade
        validationService.validateBook(BookMapper.toEntity(bookDTO));

        // Atualizando o livro
        if (bookService.update(bookDTO)) {
            // Criando a resposta de sucesso
            SuccessResponse successResponse = new SuccessResponse("Livro atualizado com sucesso", bookDTO);
            return ResponseEntity.ok(successResponse);
        }
          // Caso o livro não seja encontrado
          ErrorResponse errorResponse = new ErrorResponse("Livro não encontrado para atualização");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    } catch (IllegalArgumentException e) {
        // Criando a resposta de erro de validação
        ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


@PatchMapping("/{id}/status")
@Operation(summary = "Alterar status do livro", description = "Modifica o status de disponibilidade do livro")
@ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
@ApiResponse(responseCode = "400", description = "Status inválido")
public ResponseEntity<Object> updateStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
    try {
        // Convertendo a string de status para o enum BookStatus
        BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());

        // Atualizando o status do livro
        if (bookService.updateStatus(id, bookStatus)) {
            // Se o livro foi encontrado e o status foi atualizado com sucesso, retornamos uma resposta de sucesso
            SuccessResponse successResponse = new SuccessResponse("Status atualizado com sucesso", bookStatus);
            return ResponseEntity.ok(successResponse);
        }

        // Caso o livro não seja encontrado para atualização
        ErrorResponse errorResponse = new ErrorResponse("Livro não encontrado para atualizar o status");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    } catch (IllegalArgumentException e) {
        // Se ocorrer um erro ao tentar converter o status para o enum
        ErrorResponse errorResponse = new ErrorResponse("Status inválido: " + status);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


@DeleteMapping("/{id}")
@Operation(summary = "Excluir livro", description = "Remove um livro do sistema")
@ApiResponse(responseCode = "204", description = "Livro excluído com sucesso")
@ApiResponse(responseCode = "404", description = "Livro não encontrado")
public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
    try {
        // Tentando deletar o livro
        if (bookService.delete(id)) {
            // Se a exclusão for bem-sucedida, retornamos uma resposta de sucesso
            SuccessResponse successResponse = new SuccessResponse("Livro excluído com sucesso", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(successResponse);
        }
        
        // Se o livro não for encontrado, retornamos uma resposta de erro
        ErrorResponse errorResponse = new ErrorResponse("Livro não encontrado com o ID " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        
    } catch (IllegalArgumentException e) {
        // Se ocorrer algum erro no processo de exclusão
        ErrorResponse errorResponse = new ErrorResponse("Erro ao deletar livro: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


}
