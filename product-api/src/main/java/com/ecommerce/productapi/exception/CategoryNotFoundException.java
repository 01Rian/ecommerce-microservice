package com.ecommerce.productapi.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {
    private static final String RESOURCE_NAME = "Categoria";

    public CategoryNotFoundException(String fieldName, Object fieldValue) {
        super(RESOURCE_NAME, fieldName, fieldValue);
    }
}
