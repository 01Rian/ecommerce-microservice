package com.ecommerce.shoppingapi.exception.advice;

import com.ecommerce.shoppingapi.domain.dto.ErrorDto;
import com.ecommerce.shoppingapi.exception.ProductNotFoundException;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.ecommerce.shoppingapi.controllers")
public class ShoppingControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorDto handleProductNotFoundException(ProductNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Product Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleCategoryNotFoundException(UserNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("User Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShoppingNotFoundException.class)
    public ErrorDto handleShoppingNotFoundException(ShoppingNotFoundException exception) {
        return ErrorDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Shopping Not Found")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
