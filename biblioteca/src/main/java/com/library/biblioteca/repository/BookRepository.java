package com.library.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByTitle(String title);
    
    List<Book> findByAuthor(String author);
    
    List<Book> findByStatus(BookStatus status);
}