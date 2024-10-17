package com.library.biblioteca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.biblioteca.model.Book;
import com.library.biblioteca.model.BookStatus;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // Método para buscar livros pelo título
    List<Book> findByTitle(String title);
    
    // Método para buscar livros pelo autor
    List<Book> findByAuthor(String author);
    
    // Método para buscar livros pelo status (AVAILABLE ou BORROWED)
    List<Book> findByStatus(BookStatus status);
}