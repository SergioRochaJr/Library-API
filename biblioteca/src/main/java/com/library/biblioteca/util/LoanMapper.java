package com.library.biblioteca.util;

import com.library.biblioteca.model.Loan;
import com.library.biblioteca.dto.LoanDTO;

import java.util.List;
import java.util.stream.Collectors;

public class LoanMapper {

    // Método para mapear Loan para LoanDTO
    public static LoanDTO toDTO(Loan loan) {
        return new LoanDTO(
                loan.getId(),
                loan.getCustomer(),
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
        loan.setCustomer(loanDTO.getCustomer());
        loan.setBooks(loanDTO.getBooks());
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setReturnDate(loanDTO.getReturnDate());
        loan.setStatus(loanDTO.getStatus());
        return loan;
    }
}
