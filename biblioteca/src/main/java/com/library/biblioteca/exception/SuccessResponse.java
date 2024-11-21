package com.library.biblioteca.exception;

public class SuccessResponse {
    private String message;
    private Object data;

    // Construtor
    public SuccessResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    // Getters e Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
