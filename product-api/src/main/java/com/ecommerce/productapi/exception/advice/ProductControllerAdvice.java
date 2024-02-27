package com.ecommerce.productapi.exception.advice;

import com.ecommerce.productapi.domain.dto.ErrorDto;
import com.ecommerce.productapi.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice(basePackages = "com.ecommerce.productapi.controllers")
public class ProductControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorDto handleProductNotFoundException(ProductNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Product Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CategoryNotFoundException.class)
    public ErrorDto handleCategoryNotFoundException(CategoryNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Category Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ErrorDto handleProductAlreadyExistsException(ProductAlreadyExistsException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Product Identifier Already Exists")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder sb = new StringBuilder("Invalid Value For Field(s):");
        for (FieldError fieldError : fieldErrors) {
            sb.append(" ");
            sb.append("[").append(fieldError.getField()).append("]");
        }

        return ErrorDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(sb.toString())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
