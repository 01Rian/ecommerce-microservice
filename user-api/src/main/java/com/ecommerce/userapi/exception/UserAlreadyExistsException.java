package com.ecommerce.userapi.exception;

public class UserAlreadyExistsException extends ResourceConflictException {
    private static final String RESOURCE_NAME = "Usuário";

    public UserAlreadyExistsException(String fieldName, Object fieldValue) {
        super(RESOURCE_NAME, fieldName, fieldValue);
    }
}
