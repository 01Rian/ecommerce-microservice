package com.ecommerce.productapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductAlreadyExistsException extends BaseException {
    
    private static final String DEFAULT_MESSAGE = "Produto já existe";
    
    public ProductAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
    
    public ProductAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProductAlreadyExistsException(String productName) {
        super(String.format("Produto já existe com o nome: %s", productName));
    }
}
