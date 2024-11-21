    package com.library.biblioteca.dto;

    import java.time.LocalDate;
    import java.util.List;

    import com.library.biblioteca.model.Book;
    import com.library.biblioteca.model.LoanStatus;

    public class LoanDTO {

        private Long id;
        private CustomerDTO customer;  // Mudando de Customer para CustomerDTO
        private List<Book> books;
        private LocalDate loanDate;
        private LocalDate returnDate;
        private LoanStatus status;

        // Construtor sem argumentos
        public LoanDTO() {}

        // Construtor com todos os parâmetros
        public LoanDTO(Long id, CustomerDTO customer, List<Book> books, LocalDate loanDate, LocalDate returnDate, LoanStatus status) {
            this.id = id;
            this.customer = customer;
            this.books = books;
            this.loanDate = loanDate;
            this.returnDate = returnDate;
            this.status = status;
        }

        // Getters e Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public CustomerDTO getCustomer() {
            return customer;
        }

        public void setCustomer(CustomerDTO customer) {
            this.customer = customer;
        }

        public List<Book> getBooks() {
            return books;
        }

        public void setBooks(List<Book> books) {
            this.books = books;
        }

        public LocalDate getLoanDate() {
            return loanDate;
        }

        public void setLoanDate(LocalDate loanDate) {
            this.loanDate = loanDate;
        }

        public LocalDate getReturnDate() {
            return returnDate;
        }

        public void setReturnDate(LocalDate returnDate) {
            this.returnDate = returnDate;
        }

        public LoanStatus getStatus() {
            return status;
        }

        public void setStatus(LoanStatus status) {
            this.status = status;
        }
    }
