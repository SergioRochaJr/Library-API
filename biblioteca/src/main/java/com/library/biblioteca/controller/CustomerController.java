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
import com.library.biblioteca.exception.SuccessResponse;
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
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String name) {
        List<Customer> customers;
        if (name != null && !name.isEmpty()) {
            customers = customerService.findByName(name);
        } else {
            customers = customerService.findAll();
        }
        if (!customers.isEmpty()) {
            List<CustomerDTO> customerDTOs = customers.stream()
                                                      .map(CustomerMapper::toDTO)
                                                      .collect(Collectors.toList());
            SuccessResponse successResponse = new SuccessResponse("Lista de clientes retornada com sucesso!", customerDTOs);
            return ResponseEntity.ok(successResponse);
    } else{
        ErrorResponse errorResponse = new ErrorResponse("Nenhum cliente encontrado com o nome: " + name);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}


    @GetMapping("/{id}")
    @Operation(summary = "Obter cliente por ID", description = "Busca os detalhes de um cliente pelo ID")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            CustomerDTO customerDTO = CustomerMapper.toDTO(customer);
            SuccessResponse successResponse = new SuccessResponse("Cliente encontrado com sucesso!", customerDTO);
        return ResponseEntity.ok(successResponse);
        }
    ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado com o ID " + id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }

    @GetMapping("/birthdate/{birthDate}")
    @Operation(
        summary = "Buscar clientes por data de nascimento",
        description = "Retorna uma lista de clientes que possuem a data de nascimento fornecida."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clientes encontrados", 
                     content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class)))),
        @ApiResponse(responseCode = "404", description = "Nenhum cliente encontrado para a data de nascimento fornecida", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Object> getByBirthDate(@PathVariable("birthDate") LocalDate birthDate) {
        List<Customer> customers = customerService.findByBirthDate(birthDate);
        
        if (!customers.isEmpty()) {
            List<CustomerDTO> customerDTOs = customers.stream()
                                                      .map(CustomerMapper::toDTO)
                                                      .collect(Collectors.toList());
            SuccessResponse successResponse = new SuccessResponse("Clientes encontrados com sucesso!", customerDTOs);
            return ResponseEntity.ok(successResponse);
        }
        
        ErrorResponse errorResponse = new ErrorResponse("Nenhum cliente encontrado para a data de nascimento: " + birthDate);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    

    @PostMapping
@Operation(summary = "Criar cliente", description = "Adiciona um novo cliente ao sistema")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
    @ApiResponse(responseCode = "400", description = "Erro de validação", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
})
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
        
    
        SuccessResponse successResponse = new SuccessResponse("Cliente criado com sucesso!", CustomerMapper.toDTO(customer));
        return ResponseEntity.created(location).body(successResponse);
        
    } catch (IllegalArgumentException e) {
   
        ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}


@PutMapping("/{id}")
@Operation(summary = "Atualizar cliente", description = "Atualiza as informações de um cliente existente")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class))),
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(responseCode = "400", description = "Erro de validação", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
})
public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody CustomerDTO customerDTO) {
    try {
    
        Customer customer = CustomerMapper.toEntity(customerDTO);

        validationService.validateCustomer(customer);


        if (customerService.update(id, customerDTO)) {
            SuccessResponse successResponse = new SuccessResponse("Cliente atualizado com sucesso!", CustomerMapper.toDTO(customer));
            return ResponseEntity.ok(successResponse);  
        }

   
        ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado com o ID " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    } catch (IllegalArgumentException e) {

        ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);  
    }
}


@PatchMapping("/{id}/status")
@Operation(summary = "Alterar status do cliente", description = "Modifica o status do cliente")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
})
public ResponseEntity<Object> updateStatus(@PathVariable Long id, @RequestParam CustomerStatus status) {

    if (customerService.updateStatus(id, status)) {

        SuccessResponse successResponse = new SuccessResponse("Status do cliente atualizado com sucesso!", null);
        return ResponseEntity.ok(successResponse);  
    }

   
    ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado com o ID " + id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
}


@DeleteMapping("/{id}")
@Operation(summary = "Excluir cliente", description = "Remove um cliente do sistema")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Cliente excluído com sucesso", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", 
                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
})
public ResponseEntity<Object> delete(@PathVariable Long id) {

    if (customerService.delete(id)) {
 
        SuccessResponse successResponse = new SuccessResponse("Cliente excluído com sucesso!", null);
        return ResponseEntity.ok(successResponse);  
    }


    ErrorResponse errorResponse = new ErrorResponse("Cliente não encontrado com o ID " + id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); 
}


}
