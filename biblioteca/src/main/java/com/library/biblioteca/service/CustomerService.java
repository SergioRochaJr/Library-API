package com.library.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.biblioteca.exception.CustomerValidation;
import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.CustomerStatus;
import com.library.biblioteca.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.orElse(null);
    }

    public List<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }

    public List<Customer> findByBirthDate(LocalDate birthDate) {
        return customerRepository.findByBirthDate(birthDate);
    }

    // Método para criar um novo cliente com validação
    public void create(Customer customer) {
        // Chama a validação antes de salvar o cliente
        CustomerValidation.validate(customer);
        customerRepository.save(customer);
    }

    // Método para atualizar um cliente com validação
    public boolean update(Long id, Customer updatedCustomer) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            
            // Chama a validação antes de atualizar
            CustomerValidation.validate(updatedCustomer);
            
            customer.setName(updatedCustomer.getName());
            customer.setLastname(updatedCustomer.getLastname());
            customer.setAddress(updatedCustomer.getAddress());
            customer.setCity(updatedCustomer.getCity());
            customer.setState(updatedCustomer.getState());
            customer.setCountry(updatedCustomer.getCountry());
            customer.setBirthDate(updatedCustomer.getBirthDate());
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    // Método para atualizar o status do cliente
    public boolean updateStatus(Long id, CustomerStatus status) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            customer.setStatus(status);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }

    // Método para deletar o cliente, somente se ele não tiver empréstimos
    public boolean delete(Long id) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            if (customer.getLoans().isEmpty()) {
                customerRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
}
