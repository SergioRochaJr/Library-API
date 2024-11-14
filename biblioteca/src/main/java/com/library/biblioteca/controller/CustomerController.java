package com.library.biblioteca.controller;

import java.net.URI;
import java.time.LocalDate;
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

import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.CustomerStatus;
import com.library.biblioteca.model.ErrorResponse;
import com.library.biblioteca.service.CustomerService;
import com.library.biblioteca.service.ValidationService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ValidationService validationService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAll(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            List<Customer> customers = customerService.findByName(name);
            if (!customers.isEmpty()) {
                return ResponseEntity.ok(customers);
            }
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(customerService.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.findById(id);
        if (customer != null) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/birthdate/{birthDate}")
    public ResponseEntity<List<Customer>> getByBirthDate(@PathVariable("birthDate") LocalDate birthDate) {
        List<Customer> customers = customerService.findByBirthDate(birthDate);
        if (!customers.isEmpty()) {
            return ResponseEntity.ok(customers);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Customer customer) {
        try {
            validationService.validateCustomer(customer);
            customerService.create(customer);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(customer.getId())
                    .toUri();
            return ResponseEntity.created(location).body(customer);
        } catch (IllegalArgumentException e) {
            ErrorResponse errorResponse = new ErrorResponse("Erro de validação: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Customer customer) {
        try {
            validationService.validateCustomer(customer);
            if (customerService.update(id, customer)) {
                return ResponseEntity.ok(customer);
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
