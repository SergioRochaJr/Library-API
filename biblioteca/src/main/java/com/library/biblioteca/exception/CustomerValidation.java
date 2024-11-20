package com.library.biblioteca.exception;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.CustomerStatus;

public class CustomerValidation {

    public static void validate(Customer customer) {
        validateName(customer.getName());
        validateLastname(customer.getLastname());
        validateAddress(customer.getAddress());
        validateCity(customer.getCity());
        validateState(customer.getState());
        validateCountry(customer.getCountry());
        validateBirthDate(customer.getBirthDate());
        validateStatus(customer.getStatus());
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("O nome não pode ser vazio ou nulo.");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new ValidationException("O nome deve ter entre 3 e 50 caracteres.");
        }
    }

    private static void validateLastname(String lastname) {
        if (lastname == null || lastname.trim().isEmpty()) {
            throw new ValidationException("O sobrenome não pode ser vazio ou nulo.");
        }
        if (lastname.length() < 3 || lastname.length() > 50) {
            throw new ValidationException("O sobrenome deve ter entre 3 e 50 caracteres.");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new ValidationException("O endereço não pode ser vazio ou nulo.");
        }
        if (address.length() < 5 || address.length() > 100) {
            throw new ValidationException("O endereço deve ter entre 5 e 100 caracteres.");
        }
    }

    private static void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new ValidationException("A cidade não pode ser vazia ou nula.");
        }
        if (city.length() < 3 || city.length() > 50) {
            throw new ValidationException("A cidade deve ter entre 3 e 50 caracteres.");
        }
    }

    private static void validateState(BigDecimal state) {
        if (state == null) {
            throw new ValidationException("O estado não pode ser nulo.");
        }
        BigDecimal minState = BigDecimal.valueOf(1);
        BigDecimal maxState = BigDecimal.valueOf(50);
        if (state.compareTo(minState) < 0 || state.compareTo(maxState) > 0) {
            throw new ValidationException("O estado deve estar entre 1 e 50.");
        }
    }

    private static void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new ValidationException("O país não pode ser vazio ou nulo.");
        }
        if (country.length() < 3 || country.length() > 50) {
            throw new ValidationException("O país deve ter entre 3 e 50 caracteres.");
        }
    }

    private static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new ValidationException("A data de nascimento não pode ser nula.");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ValidationException("A data de nascimento não pode ser no futuro.");
        }
    }

    private static void validateStatus(CustomerStatus status) {
        if (status == null) {
            throw new ValidationException("O status do cliente não pode ser nulo.");
        }
    }
}
