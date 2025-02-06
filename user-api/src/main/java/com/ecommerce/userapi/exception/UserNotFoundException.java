package com.ecommerce.userapi.exception;

public class UserNotFoundException extends ResourceNotFoundException {
    private static final String RESOURCE_NAME = "Usuário";

    public UserNotFoundException(String fieldName, Object fieldValue) {
        super(RESOURCE_NAME, fieldName, fieldValue);
    }
}
