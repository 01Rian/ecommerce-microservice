package com.ecommerce.userapi.exception.advice;

import com.ecommerce.userapi.domain.dto.ErrorDto;
import com.ecommerce.userapi.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDto> handleBaseException(BaseException ex) {
        ErrorDto errorDto = new ErrorDto(
                ex.getStatus().value(),
                ex.getMessage(),
                ex.getErrorCode(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorDto errorDto = new ErrorDto(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                "VALIDATION_ERROR",
                LocalDateTime.now(),
                errors
        );
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleGenericException(Exception ex) {
        ErrorDto errorDto = new ErrorDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 