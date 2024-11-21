package com.library.biblioteca.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.library.biblioteca.dto.LoanDTO;
import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.model.LoanStatus;
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

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empréstimo por ID", description = "Retorna o empréstimo com o ID fornecido.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo encontrado", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<LoanDTO> getById(@PathVariable Long id) {
        Loan loan = loanService.findById(id);
        if (loan != null) {
            CustomerDTO customerDTO = new CustomerDTO(loan.getCustomer().getId(),
                    loan.getCustomer().getName(),
                    loan.getCustomer().getLastname(),
                    loan.getCustomer().getAddress(),
                    loan.getCustomer().getCity(),
                    loan.getCustomer().getState(),
                    loan.getCustomer().getCountry(),
                    loan.getCustomer().getBirthDate(),
                    loan.getCustomer().getStatus());
            LoanDTO loanDTO = new LoanDTO(loan.getId(), customerDTO, loan.getBooks(), loan.getLoanDate(), loan.getReturnDate(), loan.getStatus());
            return ResponseEntity.ok(loanDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Listar todos os empréstimos", description = "Retorna uma lista com todos os empréstimos registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class))))
    public ResponseEntity<List<LoanDTO>> getAll() {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findAll());
        return ResponseEntity.ok(loanDTOs);
    }

    @GetMapping("/date")
    @Operation(summary = "Buscar empréstimos por período", description = "Retorna empréstimos em um determinado período de tempo.")
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos no período", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class))))
    public ResponseEntity<List<LoanDTO>> getByDateRange(@RequestParam("startDate") LocalDate startDate, @RequestParam("endDate") LocalDate endDate) {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findByLoanDateBetween(startDate, endDate));
        return ResponseEntity.ok(loanDTOs);
    }

    @PostMapping
    @Operation(summary = "Criar um novo empréstimo", description = "Cria um novo empréstimo, associando o cliente e o livro correspondentes.")
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

    @PatchMapping("/{id}")
    @Operation(summary = "Prorrogar um empréstimo", description = "Prorroga a data de retorno de um empréstimo.")
    @ApiResponse(responseCode = "200", description = "Empréstimo prorrogado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class)))
    @ApiResponse(responseCode = "400", description = "Nova data de retorno inválida")
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    public ResponseEntity<Object> extendLoan(@PathVariable Long id, @RequestBody LocalDate newReturnDate) {
        Loan loan = loanService.findById(id);
        if (loan != null) {
            // Validação: a nova data de retorno não pode ser menor que a data de retirada
            if (newReturnDate.isBefore(loan.getLoanDate())) {
                return ResponseEntity.badRequest().body(new ErrorResponse("A nova data de retorno não pode ser menor que a data de retirada."));
            }
            loan.setReturnDate(newReturnDate);
            loanService.update(loan);
            return ResponseEntity.ok(LoanMapper.toDTO(loan));
        }
        return ResponseEntity.notFound().build();
    }
    

    @PutMapping("/{id}")
    @Operation(summary = "Finalizar um empréstimo", description = "Finaliza o empréstimo, atualizando o status dos livros para 'AVAILABLE'.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Empréstimo finalizado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
        @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<Object> finishLoan(@PathVariable Long id) {
        Loan loan = loanService.findById(id);
        if (loan != null) {
            loan.setStatus(LoanStatus.RETURNED);
            loanService.update(loan);
            return ResponseEntity.ok(LoanMapper.toDTO(loan));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um empréstimo", description = "Exclui um empréstimo com base no ID fornecido.")
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
