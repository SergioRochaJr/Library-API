package com.library.biblioteca.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.library.biblioteca.model.Customer;
import com.library.biblioteca.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

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
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        customerService.create(customer);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(customer.getId())
                .toUri();
        return ResponseEntity.created(location).body(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        if (customerService.update(id, customer)) {
            return ResponseEntity.ok(customer);
        }
        return ResponseEntity.notFound().build();
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
