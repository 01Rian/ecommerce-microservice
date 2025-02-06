package com.ecommerce.userapi.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Recurso não encontrado";
    private static final HttpStatus DEFAULT_STATUS = HttpStatus.NOT_FOUND;
    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException() {
        super(DEFAULT_MESSAGE, DEFAULT_STATUS, ERROR_CODE);
    }

    public ResourceNotFoundException(String message) {
        super(message, DEFAULT_STATUS, ERROR_CODE);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado com %s: '%s'", resourceName, fieldName, fieldValue),
                DEFAULT_STATUS, ERROR_CODE);
    }
} 