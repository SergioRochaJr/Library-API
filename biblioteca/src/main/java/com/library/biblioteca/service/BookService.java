package com.library.biblioteca.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.library.biblioteca.model.Book;

@Service
public class BookService {

    public List<Book> findAll() {
        // Implementar a lógica para buscar todos os livros
        return null;
    }

    public Book findById(Long id) {
        // Implementar a lógica para buscar um livro pelo ID
        return null;
    }

    public void create(Book book) {
        // Implementar a lógica para criar um novo livro
    }

    public boolean update(Book book) {
        // Implementar a lógica para atualizar um livro
        return false;
    }

    public boolean updateStatus(Long id, String status) {
        // Implementar a lógica para atualizar o status do livro
        return false;
    }

    public boolean delete(Long id) {
        // Implementar a lógica para inativar um livro
        return false;
    }
}