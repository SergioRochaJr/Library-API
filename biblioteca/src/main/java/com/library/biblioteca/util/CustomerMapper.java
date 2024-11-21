package com.library.biblioteca.util;

import com.library.biblioteca.dto.CustomerDTO;
import com.library.biblioteca.model.Customer;

public class CustomerMapper {

    public static CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getLastname(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getCountry(),
                customer.getBirthDate(),
                customer.getStatus()
        );
    }

    public static Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }
        return new Customer(
                customerDTO.getId(),
                customerDTO.getName(),
                customerDTO.getLastname(),
                customerDTO.getAddress(),
                customerDTO.getCity(),
                customerDTO.getState(),
                customerDTO.getCountry(),
                customerDTO.getBirthDate(),
                customerDTO.getStatus()
        );
    }
}
