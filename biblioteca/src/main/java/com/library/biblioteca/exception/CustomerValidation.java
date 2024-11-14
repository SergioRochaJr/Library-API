package com.library.biblioteca.exception;

import java.time.LocalDate;

import com.library.biblioteca.model.Customer;

public class CustomerValidation {

    public static void validate(Customer customer) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new ValidationException("O nome do cliente é obrigatório.");
        }

        if (customer.getBirthDate() == null) {
            throw new ValidationException("A data de nascimento é obrigatória.");
        } else if (customer.getBirthDate().isAfter(LocalDate.now())) {
            throw new ValidationException("A data de nascimento não pode ser no futuro.");
        }
    }
}
