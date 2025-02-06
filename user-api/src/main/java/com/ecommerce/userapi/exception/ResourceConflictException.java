package com.ecommerce.userapi.exception;

import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BaseException {
    private static final String DEFAULT_MESSAGE = "Conflito de recurso";
    private static final HttpStatus DEFAULT_STATUS = HttpStatus.CONFLICT;
    private static final String ERROR_CODE = "RESOURCE_CONFLICT";

    public ResourceConflictException() {
        super(DEFAULT_MESSAGE, DEFAULT_STATUS, ERROR_CODE);
    }

    public ResourceConflictException(String message) {
        super(message, DEFAULT_STATUS, ERROR_CODE);
    }

    public ResourceConflictException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s já existe com %s: '%s'", resourceName, fieldName, fieldValue),
                DEFAULT_STATUS, ERROR_CODE);
    }
} 