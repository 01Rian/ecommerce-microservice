package com.ecommerce.userapi.exception.advice;

import com.ecommerce.userapi.domain.dto.ErrorDto;
import com.ecommerce.userapi.exception.UserAlreadyExistsException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.ecommerce.userapi.controller")
public class UserControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleUserNotFoundException(UserNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("User Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ErrorDto handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("User Already Exists")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
