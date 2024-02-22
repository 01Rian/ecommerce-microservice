package com.ecommerce.shoppingapi.exception.advice;

import com.ecommerce.shoppingapi.domain.dto.ErrorDto;
import com.ecommerce.shoppingapi.exception.ProductNotFoundException;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice(basePackages = "com.ecommerce.shoppingapi.controllers")
public class ShoppingControllerAdvice {

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
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleCategoryNotFound(UserNotFoundException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setMessage("User Not Found");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShoppingNotFoundException.class)
    public ErrorDto handleShoppingNotFound(ShoppingNotFoundException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorDto.setMessage("Shopping Not Found");
        errorDto.setTimestamp(LocalDateTime.now());
        return errorDto;
    }
}
