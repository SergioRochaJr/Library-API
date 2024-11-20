package com.library.biblioteca.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.library.biblioteca.model.CustomerStatus;

public class CustomerDTO {

    private Long id;
    private String name;
    private String lastname;
    private String address;
    private String city;
    private BigDecimal state;
    private String country;
    private LocalDate birthDate;
    private CustomerStatus status;

    // Construtor padrão
    public CustomerDTO() {}

    // Construtor com todos os parâmetros
    public CustomerDTO(Long id, String name, String lastname, String address, String city, BigDecimal state, 
                       String country, LocalDate birthDate, CustomerStatus status) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.birthDate = birthDate;
        this.status = status;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getState() {
        return state;
    }

    public void setState(BigDecimal state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    // toString (opcional, pode ser útil para depuração ou log)
    @Override
    public String toString() {
        return "CustomerDTO [id=" + id + ", name=" + name + ", lastname=" + lastname + ", address=" + address
                + ", city=" + city + ", state=" + state + ", country=" + country + ", birthDate=" + birthDate
                + ", status=" + status + "]";
    }
}
