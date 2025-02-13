package com.ecommerce.productapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends BaseException {
    
    private static final String DEFAULT_MESSAGE = "Produto não encontrado";
    
    public ProductNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProductNotFoundException(Long id) {
        super(String.format("Produto não encontrado com ID: %d", id));
    }
}
