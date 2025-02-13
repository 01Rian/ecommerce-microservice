package com.ecommerce.productapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends BaseException {
    
    private static final String DEFAULT_MESSAGE = "Categoria não encontrada";
    
    public CategoryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
    public CategoryNotFoundException(String message) {
        super(message);
    }
    
    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CategoryNotFoundException(Long id) {
        super(String.format("Categoria não encontrada com ID: %d", id));
    }
}
