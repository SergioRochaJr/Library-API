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

import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.dto.LoanDTO;
import com.library.biblioteca.service.LoanService;
import com.library.biblioteca.service.ValidationService;
import com.library.biblioteca.util.LoanMapper;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAll() {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findAll());
        return ResponseEntity.ok(loanDTOs);
    }

    @GetMapping("/{id}")
public ResponseEntity<LoanDTO> getById(@PathVariable Long id) {
    Loan loan = loanService.findById(id);
    if (loan != null) {
        // Convertendo o Loan para LoanDTO, com CustomerDTO no lugar de Customer
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


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanDTO>> getByCustomerId(@PathVariable Long customerId) {
        List<LoanDTO> loanDTOs = LoanMapper.toDTOList(loanService.findByCustomerId(customerId));
        return ResponseEntity.ok(loanDTOs);
    }

    @PostMapping
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
