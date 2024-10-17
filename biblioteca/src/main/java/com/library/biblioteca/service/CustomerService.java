package com.library.biblioteca.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.library.biblioteca.model.Customer;
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

    public void create(Customer customer) {
        customerRepository.save(customer);
    }

    public boolean update(Long id, Customer updatedCustomer) {
        Optional<Customer> existingCustomer = customerRepository.findById(id);
        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
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

    // public boolean updateStatus(Long id, String status) {
    //     Optional<Customer> existingCustomer = customerRepository.findById(id);
    //     if (existingCustomer.isPresent()) {
    //         Customer customer = existingCustomer.get();
    //         customer.setStatus(status);
    //         customerRepository.save(customer);
    //         return true;
    //     }
    //     return false;
    // }

    public boolean delete(Long id) {
        if (customerRepository.existsByIdAndLoansEmpty(id)) { 
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
