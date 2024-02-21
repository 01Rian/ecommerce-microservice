package com.ecommerce.productapi.productapi.exception.advice;

import com.ecommerce.productapi.productapi.domain.dto.ErrorDto;
import com.ecommerce.productapi.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.productapi.exception.ProductAlreadyExistsException;
import com.ecommerce.productapi.productapi.exception.ProductNotFoundException;
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

@ControllerAdvice(basePackages = "com.ecommerce.productapi.productapi.controllers")
public class ProductControllerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorDto handleProductNotFound(ProductNotFoundException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setMessage("Product Not Found");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CategoryNotFoundException.class)
    public ErrorDto handleCategoryNotFound(CategoryNotFoundException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setMessage("Category Not Found");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ErrorDto handleProductAlreadyExistsException(ProductAlreadyExistsException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDto.setMessage("Product Identifier Already Exists");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto handleArgumentNotValidException(MethodArgumentNotValidException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.BAD_REQUEST.value());

        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder sb = new StringBuilder("Invalid Value For Field(s):");
        for (FieldError fieldError : fieldErrors) {
            sb.append(" ");
            sb.append("[").append(fieldError.getField()).append("]");
        }

        errorDto.setMessage(sb.toString());
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }
}
