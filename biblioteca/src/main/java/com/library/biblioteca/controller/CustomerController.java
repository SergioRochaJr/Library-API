package com.library.biblioteca.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.CustomerStatus;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.service.CustomerService;
import com.library.biblioteca.service.ValidationService;
import com.library.biblioteca.util.CustomerMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/customers")
@Tag(name = "Customers", description = "Endpoints para gerenciamento de clientes")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Retorna todos os clientes ou busca por nome")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    public ResponseEntity<List<CustomerDTO>> getAll(@RequestParam(required = false) String name) {
        List<Customer> customers;
        if (name != null && !name.isEmpty()) {
            customers = customerService.findByName(name);
        } else {
            customers = customerService.findAll();
        }
        List<CustomerDTO> customerDTOs = customers.stream()
                                                  .map(CustomerMapper::toDTO)
                                                  .collect(Collectors.toList());
        return ResponseEntity.ok(customerDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter cliente por ID", description = "Busca os detalhes de um cliente pelo ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            return ResponseEntity.ok(CustomerMapper.toDTO(customer));
        }
        return ResponseEntity.notFound().build();
    }

@GetMapping("/birthdate/{birthDate}")
@Operation(
    summary = "Buscar clientes por data de nascimento",
    description = "Retorna uma lista de clientes que possuem a data de nascimento fornecida."
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Clientes encontrados", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class)))),
    @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado para a data de nascimento fornecida")
})
    public ResponseEntity<List<CustomerDTO>> getByBirthDate(@PathVariable("birthDate") LocalDate birthDate) {
        List<Customer> customers = customerService.findByBirthDate(birthDate);
        if (!customers.isEmpty()) {
            List<CustomerDTO> customerDTOs = customers.stream()
                                                      .map(CustomerMapper::toDTO)
                                                      .collect(Collectors.toList());
            return ResponseEntity.ok(customerDTOs);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Criar cliente", description = "Adiciona um novo cliente ao sistema")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro de validação")
    public ResponseEntity<Object> create(@RequestBody CustomerDTO customerDTO) {
        try {
            Customer customer = CustomerMapper.toEntity(customerDTO);
            validationService.validateCustomer(customer);
            customerService.create(customerDTO);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(customer.getId())
                    .toUri();
            return ResponseEntity.created(location).body(CustomerMapper.toDTO(customer));
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Atualiza as informações de um cliente existente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody com.library.biblioteca.dto.CustomerDTO customerDTO) {
        try {
            Customer customer = CustomerMapper.toEntity(customerDTO);
            validationService.validateCustomer(customer);
            if (customerService.update(id, customerDTO)) {
                return ResponseEntity.ok(CustomerMapper.toDTO(customer));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status do cliente", description = "Modifica o status do cliente")
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam CustomerStatus status) {
        if (customerService.updateStatus(id, status)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir cliente", description = "Remove um cliente do sistema")
    @ApiResponse(responseCode = "204", description = "Cliente excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (customerService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
