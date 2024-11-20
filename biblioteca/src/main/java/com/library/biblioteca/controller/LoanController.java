package com.library.biblioteca.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.library.biblioteca.dto.LoanDTO;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.service.LoanService;
import com.library.biblioteca.service.ValidationService;
import com.library.biblioteca.util.LoanMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loan", description = "Endpoints para gerenciamento de empréstimos")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    @Operation(
        summary = "Listar todos os empréstimos",
        description = "Retorna uma lista com todos os empréstimos registrados."
    )
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class))))
    public ResponseEntity<List<LoanDTO>> getAll() {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findAll());
        return ResponseEntity.ok(loanDTOs);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar empréstimo por ID",
        description = "Retorna o empréstimo com o ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo encontrado", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<LoanDTO> getById(@PathVariable Long id) {
        Loan loan = loanService.findById(id);
        if (loan != null) {
            LoanDTO loanDTO = LoanMapper.toDTO(loan);
            return ResponseEntity.ok(loanDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
        summary = "Buscar empréstimos por ID de cliente",
        description = "Retorna uma lista de empréstimos vinculados ao cliente com o ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de empréstimos do cliente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class)))),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    public ResponseEntity<List<LoanDTO>> getByCustomerId(@PathVariable Long customerId) {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findByCustomerId(customerId));
        return ResponseEntity.ok(loanDTOs);
    }

    @PostMapping
    @Operation(
        summary = "Criar um novo empréstimo",
        description = "Cria um novo empréstimo, associando o cliente e o livro correspondentes."
    )
    @ApiResponse(responseCode = "201", description = "Empréstimo criado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class)))
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    public ResponseEntity<Object> create(@RequestBody LoanDTO loanDTO) {
        try {
            Loan loan = LoanMapper.toEntity(loanDTO);
            validationService.validateLoan(loan);
            Loan createdLoan = loanService.create(loan);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdLoan.getId())
                    .toUri();
            return ResponseEntity.created(location).body(LoanMapper.toDTO(createdLoan));
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Atualizar um empréstimo",
        description = "Atualiza as informações de um empréstimo existente com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo atualizado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody com.library.biblioteca.dto.LoanDTO loanDTO) {
        try {
            Loan loan = LoanMapper.toEntity(loanDTO);
            loan.setId(id);
            validationService.validateLoan(loan);
            if (loanService.update(loan)) {
                return ResponseEntity.ok(LoanMapper.toDTO(loan));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir um empréstimo",
        description = "Exclui um empréstimo com base no ID fornecido."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Empréstimo excluído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
