package com.ecommerce.shoppingapi.exception.advice;

import com.ecommerce.shoppingapi.domain.dto.error.ErrorResponseDto;
import com.ecommerce.shoppingapi.exception.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    // Constantes de erros e códigos
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Recurso não encontrado";
    private static final String RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";
    private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
    private static final String UNSUPPORTED_MEDIA_TYPE_MESSAGE = "Tipo de conteúdo não suportado";
    private static final String UNSUPPORTED_MEDIA_TYPE_CODE = "UNSUPPORTED_MEDIA_TYPE";
    private static final String INTERNAL_SERVER_ERROR_CODE = "INTERNAL_SERVER_ERROR";
    private static final String GENERIC_ERROR_MESSAGE = "Algo deu errado";
    
    // Constantes para validação
    private static final String OBJECT_NAME = "object";
    private static final String FIELD_NOME = "nome";
    private static final String FIELD_EMAIL = "email";
    private static final String ERROR_CAMPO_VAZIO = "não pode ser vazio";
    private static final String ERROR_EMAIL_INVALIDO = "deve ser um email válido";

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Nested
    @DisplayName("Testes para exceções base")
    class BaseExceptionTests {

        @Test
        @DisplayName("Deve tratar BaseException e retornar resposta com status e mensagem corretos")
        void handleBaseException_ShouldReturn_CorrectErrorResponse() {
            // Arrange
            HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
            
            BaseException mockException = mock(BaseException.class);
            when(mockException.getMessage()).thenReturn(RESOURCE_NOT_FOUND_MESSAGE);
            when(mockException.getStatus()).thenReturn(expectedStatus);
            when(mockException.getErrorCode()).thenReturn(RESOURCE_NOT_FOUND_CODE);
            
            // Act
            ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleBaseException(mockException);
            
            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
            
            // Usando satisfies para evitar NullPointerException
            assertThat(response.getBody()).satisfies(body -> {
                assertThat(body.getStatus()).isEqualTo(expectedStatus.value());
                assertThat(body.getMessage()).isEqualTo(RESOURCE_NOT_FOUND_MESSAGE);
                assertThat(body.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND_CODE);
                assertThat(body.getTimestamp()).isNotNull();
            });
        }
    }
    
    @Nested
    @DisplayName("Testes para exceções de validação")
    class ValidationExceptionTests {

        @Test
        @DisplayName("Deve tratar MethodArgumentNotValidException e retornar erros de validação")
        void handleValidationException_ShouldReturn_ValidationErrors() {
            // Arrange
            MethodArgumentNotValidException mockException = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(OBJECT_NAME, FIELD_NOME, ERROR_CAMPO_VAZIO));
            fieldErrors.add(new FieldError(OBJECT_NAME, FIELD_EMAIL, ERROR_EMAIL_INVALIDO));
            
            when(mockException.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
            
            // Act
            ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleValidationException(mockException);
            
            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            
            // Usando satisfies para evitar NullPointerException
            assertThat(response.getBody()).satisfies(body -> {
                assertThat(body.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                assertThat(body.getMessage()).contains(FIELD_NOME + ": " + ERROR_CAMPO_VAZIO);
                assertThat(body.getMessage()).contains(FIELD_EMAIL + ": " + ERROR_EMAIL_INVALIDO);
                assertThat(body.getErrorCode()).isEqualTo(VALIDATION_ERROR_CODE);
                assertThat(body.getTimestamp()).isNotNull();
            });
        }
    }
    
    @Nested
    @DisplayName("Testes para exceções de tipo de mídia")
    class MediaTypeExceptionTests {

        @Test
        @DisplayName("Deve tratar HttpMediaTypeNotSupportedException corretamente")
        void handleMediaTypeNotSupported_ShouldReturn_UnsupportedMediaTypeResponse() {
            // Arrange
            HttpMediaTypeNotSupportedException mockException = mock(HttpMediaTypeNotSupportedException.class);
            
            // Act
            ResponseEntity<ErrorResponseDto> response = 
                exceptionHandler.handleHttpMediaTypeNotSupportedException(mockException);
            
            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            
            // Usando satisfies para evitar NullPointerException
            assertThat(response.getBody()).satisfies(body -> {
                assertThat(body.getStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
                assertThat(body.getMessage()).contains(UNSUPPORTED_MEDIA_TYPE_MESSAGE);
                assertThat(body.getErrorCode()).isEqualTo(UNSUPPORTED_MEDIA_TYPE_CODE);
                assertThat(body.getTimestamp()).isNotNull();
            });
        }
    }
    
    @Nested
    @DisplayName("Testes para exceções genéricas")
    class GenericExceptionTests {

        @Test
        @DisplayName("Deve tratar Exception genérica corretamente")
        void handleGenericException_ShouldReturn_InternalServerErrorResponse() {
            // Arrange
            Exception mockException = new Exception(GENERIC_ERROR_MESSAGE);
            
            // Act
            ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleGenericException(mockException);
            
            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            
            // Usando satisfies para evitar NullPointerException
            assertThat(response.getBody()).satisfies(body -> {
                assertThat(body.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                assertThat(body.getMessage()).contains(GENERIC_ERROR_MESSAGE);
                assertThat(body.getErrorCode()).isEqualTo(INTERNAL_SERVER_ERROR_CODE);
                assertThat(body.getTimestamp()).isNotNull();
            });
        }
    }
}