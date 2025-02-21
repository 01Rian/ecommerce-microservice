package com.ecommerce.productapi.exception;

public class ProductNotFoundException extends ResourceNotFoundException {
    private static final String RESOURCE_NAME = "Produto";

    public ProductNotFoundException(String fieldName, Object fieldValue) {
        super(RESOURCE_NAME, fieldName, fieldValue);
    }
}
