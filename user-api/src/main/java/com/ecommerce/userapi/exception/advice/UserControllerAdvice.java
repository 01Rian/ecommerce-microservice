package com.ecommerce.userapi.exception.advice;

import com.ecommerce.userapi.domain.dto.ErrorDto;
import com.ecommerce.userapi.exception.UserAlreadyExistsException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice(basePackages = "com.ecommerce.userapi.controller")
public class UserControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleUserNotFound(UserNotFoundException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setMessage("User Not Found");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ErrorDto handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDto.setMessage("User Already Exists");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }
}
