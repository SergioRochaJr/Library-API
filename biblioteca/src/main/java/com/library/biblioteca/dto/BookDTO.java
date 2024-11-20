package com.library.biblioteca.dto;

import java.time.LocalDate;

import com.library.biblioteca.model.BookStatus;

public class BookDTO {
    
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publishedDate;
    private BookStatus status;

    public BookDTO() {
    }

    public BookDTO(Long id, String title, String author, String isbn, LocalDate publishedDate, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title; 
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
