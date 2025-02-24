package com.ecommerce.shoppingapi.exception.advice;

import com.ecommerce.shoppingapi.domain.dto.error.ErrorResponseDto;
import com.ecommerce.shoppingapi.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException exception) {
        logger.error("Base exception occurred:", exception);
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(exception.getStatus().value())
                .message(exception.getMessage())
                .errorCode(exception.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(exception.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException exception) {
        logger.error("Validation exception occurred:", exception);
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder sb = new StringBuilder("Campos inválidos:");
        for (FieldError fieldError : fieldErrors) {
            sb.append(" ");
            sb.append("[").append(fieldError.getField()).append(": ")
                    .append(fieldError.getDefaultMessage()).append("]");
        }

        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(sb.toString())
                .errorCode("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception) {
        logger.error("Media type not supported:", exception);
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .message("Tipo de conteúdo não suportado. Use application/json")
                .errorCode("UNSUPPORTED_MEDIA_TYPE")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception exception) {
        logger.error("Unexpected error occurred:", exception);
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Erro interno do servidor: " + exception.getMessage())
                .errorCode("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
