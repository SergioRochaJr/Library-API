package com.library.biblioteca.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.library.biblioteca.model.Customer;
import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.model.CustomerStatus;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.service.CustomerService;
import com.library.biblioteca.service.ValidationService;
import com.library.biblioteca.util.CustomerMapper;

@RestController
@RequestMapping("api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
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
    public ResponseEntity<CustomerDTO> getById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            return ResponseEntity.ok(CustomerMapper.toDTO(customer));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/birthdate/{birthDate}")
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
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam CustomerStatus status) {
        if (customerService.updateStatus(id, status)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (customerService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
