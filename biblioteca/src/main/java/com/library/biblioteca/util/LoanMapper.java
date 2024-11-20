package com.library.biblioteca.util;

import com.library.biblioteca.model.Customer;
import com.library.biblioteca.model.Loan;
import com.library.biblioteca.dto.LoanDTO;
import com.library.biblioteca.dto.CustomerDTO;
import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {

    // Método para mapear Loan para LoanDTO
    public static LoanDTO toDTO(Loan loan) {
        // Mapeando Customer para CustomerDTO
        CustomerDTO customerDTO = new CustomerDTO(
                loan.getCustomer().getId(),
                loan.getCustomer().getName(),
                loan.getCustomer().getLastname(),
                loan.getCustomer().getAddress(),
                loan.getCustomer().getCity(),
                loan.getCustomer().getState(),
                loan.getCustomer().getCountry(),
                loan.getCustomer().getBirthDate(),
                loan.getCustomer().getStatus()
        );

        // Retorna LoanDTO com CustomerDTO
        return new LoanDTO(
                loan.getId(),
                customerDTO,  // Passa CustomerDTO
                loan.getBooks(),
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.getStatus()
        );
    }

    // Método para mapear uma lista de Loans para uma lista de LoanDTOs
    public static List<LoanDTO> toDTOList(List<Loan> loans) {
        return loans.stream()
                    .map(LoanMapper::toDTO)
                    .collect(Collectors.toList());
    }

    // Método para mapear LoanDTO para Loan (opcional, caso precise converter do DTO para a entidade)
    public static Loan toEntity(LoanDTO loanDTO) {
        Loan loan = new Loan();
        loan.setId(loanDTO.getId());
        // Mapeando CustomerDTO para Customer
        loan.setCustomer(new Customer(
                loanDTO.getCustomer().getId(),
                loanDTO.getCustomer().getName(),
                loanDTO.getCustomer().getLastname(),
                loanDTO.getCustomer().getAddress(),
                loanDTO.getCustomer().getCity(),
                loanDTO.getCustomer().getState(),
                loanDTO.getCustomer().getCountry(),
                loanDTO.getCustomer().getBirthDate(),
                loanDTO.getCustomer().getStatus()
        ));
        loan.setBooks(loanDTO.getBooks());
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setReturnDate(loanDTO.getReturnDate());
        loan.setStatus(loanDTO.getStatus());
        return loan;
    }
}
