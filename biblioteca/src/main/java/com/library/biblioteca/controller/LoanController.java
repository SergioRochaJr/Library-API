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
import com.library.biblioteca.exception.SuccessResponse;
import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.model.BookStatus;
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
public ResponseEntity<Object> getById(@PathVariable Long id) {
    Loan loan = loanService.findById(id);
    if (loan != null) {
        // Cria o DTO do cliente
        CustomerDTO customerDTO = new CustomerDTO(
                loan.getCustomer().getId(),
                loan.getCustomer().getName(),
                loan.getCustomer().getLastname(),
                loan.getCustomer().getAddress(),
                loan.getCustomer().getCity(),
                loan.getCustomer().getState(),
                loan.getCustomer().getCountry(),
                loan.getCustomer().getBirthDate(),
                loan.getCustomer().getStatus()
        );

        // Cria o DTO do empréstimo
        LoanDTO loanDTO = new LoanDTO(
                loan.getId(),
                customerDTO,
                loan.getBooks(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );

        // Retorna a resposta de sucesso
        SuccessResponse successResponse = new SuccessResponse("Empréstimo encontrado com sucesso", List.of(loanDTO));
        return ResponseEntity.ok(successResponse);
    }

    // Retorna a resposta de erro caso o empréstimo não seja encontrado
    ErrorResponse errorResponse = new ErrorResponse("Empréstimo não encontrado com o ID " + id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
}


@GetMapping
@Operation(summary = "Listar todos os empréstimos", description = "Retorna uma lista com todos os empréstimos registrados.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos retornada com sucesso", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class)))),
    @ApiResponse(responseCode = "404", description = "Nenhum empréstimo encontrado")
})
public ResponseEntity<Object> getAll() {
    List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findAll());
    if (loanDTOs.isEmpty()) {
        // Retorna uma resposta de erro caso não existam empréstimos
        ErrorResponse errorResponse = new ErrorResponse("Nenhum empréstimo encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Retorna a resposta de sucesso com a lista de empréstimos
    SuccessResponse successResponse = new SuccessResponse("Lista de empréstimos retornada com sucesso", loanDTOs);
    return ResponseEntity.ok(successResponse);
}


@GetMapping("/date")
@Operation(summary = "Buscar empréstimos por período", description = "Retorna empréstimos em um determinado período de tempo.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos no período retornada com sucesso", content = @Content(array = @ArraySchema(schema = @Schema(implementation = LoanDTO.class)))),
    @ApiResponse(responseCode = "404", description = "Nenhum empréstimo encontrado no período especificado")
})
public ResponseEntity<Object> getByDateRange(
        @RequestParam("startDate") LocalDate startDate, 
        @RequestParam("endDate") LocalDate endDate) {

    List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findByLoanDateBetween(startDate, endDate));
    if (loanDTOs.isEmpty()) {
        // Retorna uma resposta de erro caso não existam empréstimos no período
        ErrorResponse errorResponse = new ErrorResponse(
                String.format("Nenhum empréstimo encontrado entre %s e %s.", startDate, endDate)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Retorna a resposta de sucesso com a lista de empréstimos no período
    SuccessResponse successResponse = new SuccessResponse(
            "Lista de empréstimos no período retornada com sucesso", loanDTOs
    );
    return ResponseEntity.ok(successResponse);
}


@PostMapping
@Operation(summary = "Criar um novo empréstimo", description = "Cria um novo empréstimo, associando o cliente e o livro correspondentes.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Empréstimo criado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
    @ApiResponse(responseCode = "400", description = "Erro de validação")
})
public ResponseEntity<Object> create(@RequestBody LoanDTO loanDTO) {
    try {
        // Mapeia o DTO para a entidade e valida o empréstimo
        Loan loan = LoanMapper.toEntity(loanDTO);
        validationService.validateLoan(loan);

        // Cria o empréstimo e obtém a URI do recurso criado
        Loan createdLoan = loanService.create(loan);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdLoan.getId())
                .toUri();

        // Retorna a resposta de sucesso com o recurso criado
        LoanDTO createdLoanDTO = LoanMapper.toDTO(createdLoan);
        SuccessResponse successResponse = new SuccessResponse(
                "Empréstimo criado com sucesso", List.of(createdLoanDTO)
        );
        return ResponseEntity.created(location).body(successResponse);

    } catch (IllegalArgumentException e) {
        // Retorna uma resposta de erro com a mensagem de validação
        ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


@PatchMapping("/{id}")
@Operation(summary = "Prorrogar um empréstimo", description = "Prorroga a data de retorno de um empréstimo.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Empréstimo prorrogado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
    @ApiResponse(responseCode = "400", description = "Nova data de retorno inválida"),
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
})
public ResponseEntity<Object> extendLoan(@PathVariable Long id, @RequestBody LocalDate newReturnDate) {
    Loan loan = loanService.findById(id);

    if (loan == null) {
        // Resposta para empréstimo não encontrado
        ErrorResponse errorResponse = new ErrorResponse("Empréstimo com ID " + id + " não encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Validação: a nova data de retorno não pode ser menor que a data de retirada
    if (newReturnDate.isBefore(loan.getLoanDate())) {
        ErrorResponse errorResponse = new ErrorResponse("A nova data de retorno não pode ser menor que a data de retirada.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Atualiza a data de retorno e salva o empréstimo
    loan.setReturnDate(newReturnDate);
    loanService.update(loan);

    // Resposta de sucesso
    LoanDTO updatedLoanDTO = LoanMapper.toDTO(loan);
    SuccessResponse successResponse = new SuccessResponse(
            "Empréstimo prorrogado com sucesso.", List.of(updatedLoanDTO)
    );
    return ResponseEntity.ok(successResponse);
}

    

    @PutMapping("/{id}")
@Operation(summary = "Finalizar um empréstimo", description = "Finaliza o empréstimo, atualizando o status dos livros para 'AVAILABLE'.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Empréstimo finalizado com sucesso", content = @Content(schema = @Schema(implementation = LoanDTO.class))),
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
})
public ResponseEntity<Object> finishLoan(@PathVariable Long id) {
    Loan loan = loanService.findById(id);

    if (loan == null) {
        // Resposta para empréstimo não encontrado
        ErrorResponse errorResponse = new ErrorResponse("Empréstimo com ID " + id + " não encontrado.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // Atualiza o status do empréstimo para 'RETURNED'
    loan.setStatus(LoanStatus.RETURNED);

    // Atualiza o status dos livros associados para 'AVAILABLE'
    loan.getBooks().forEach(book -> book.setStatus(BookStatus.AVAILABLE));

    loanService.update(loan);

    // Resposta de sucesso
    LoanDTO updatedLoanDTO = LoanMapper.toDTO(loan);
    SuccessResponse successResponse = new SuccessResponse(
            "Empréstimo finalizado com sucesso.", List.of(updatedLoanDTO)
    );
    return ResponseEntity.ok(successResponse);
}


@DeleteMapping("/{id}")
@Operation(summary = "Excluir um empréstimo", description = "Exclui um empréstimo com base no ID fornecido.")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Empréstimo excluído com sucesso"),
    @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
})
public ResponseEntity<Object> delete(@PathVariable Long id) {
    if (loanService.delete(id)) {
        // Retorna uma mensagem de sucesso ao excluir o empréstimo
        SuccessResponse successResponse = new SuccessResponse(
                "Empréstimo com ID " + id + " excluído com sucesso.", null
        );
        return ResponseEntity.ok(successResponse);
    }
    // Resposta para empréstimo não encontrado
    ErrorResponse errorResponse = new ErrorResponse("Empréstimo com ID " + id + " não encontrado.");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
}


}
