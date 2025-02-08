package com.ecommerce.userapi.exception.advice;

import com.ecommerce.userapi.controller.UserController;
import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.exception.ResourceConflictException;
import com.ecommerce.userapi.exception.ResourceNotFoundException;
import com.ecommerce.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public GlobalExceptionHandlerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("deve retornar ErrorDto correto quando ResourceNotFoundException for lançada")
    void shouldReturnCorrectErrorDto_WhenResourceNotFoundExceptionIsThrown() throws Exception {
        // Arrange
        String message = "Usuário não encontrado com id: '1'";
        when(userService.findById(1L)).thenThrow(new ResourceNotFoundException(message));

        // Act & Assert
        mockMvc.perform(get("/users/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorDto correto quando ResourceConflictException for lançada")
    void shouldReturnCorrectErrorDto_WhenResourceConflictExceptionIsThrown() throws Exception {
        // Arrange
        String message = "Usuário já existe com email: 'test@email.com'";
        UserRequestDto userRequest = UserRequestDto.builder()
                .name("Test")
                .email("test@email.com")
                .cpf("12345678901")
                .phone("11999999999")
                .address("Test Address")
                .build();
        
        when(userService.save(any(UserRequestDto.class))).thenThrow(new ResourceConflictException(message));

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorDto correto quando MethodArgumentNotValidException for lançada")
    void shouldReturnCorrectErrorDto_WhenMethodArgumentNotValidExceptionIsThrown() throws Exception {
        // Arrange
        UserRequestDto invalidUser = UserRequestDto.builder()
                .name("")
                .email("invalid-email")
                .cpf("123")  // CPF inválido
                .phone("123") // Telefone inválido
                .address("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.name").value("O nome é obrigatório"))
                .andExpect(jsonPath("$.errors.email").value("Email inválido"))
                .andExpect(jsonPath("$.errors.cpf").value("CPF deve conter 11 dígitos numéricos"))
                .andExpect(jsonPath("$.errors.phone").value("Telefone deve conter 10 ou 11 dígitos numéricos"))
                .andExpect(jsonPath("$.errors.address").value("O endereço é obrigatório"));
    }

    @Test
    @DisplayName("deve retornar ErrorDto correto quando HttpMediaTypeNotSupportedException for lançada")
    void shouldReturnCorrectErrorDto_WhenHttpMediaTypeNotSupportedExceptionIsThrown() throws Exception {
        // Arrange
        UserRequestDto validUser = UserRequestDto.builder()
                .name("Test")
                .email("test@email.com")
                .cpf("12345678901")
                .phone("11999999999")
                .address("Test Address")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.message").value("Tipo de conteúdo não suportado. Use application/json"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorDto correto quando Exception genérica for lançada")
    void shouldReturnCorrectErrorDto_WhenGenericExceptionIsThrown() throws Exception {
        // Arrange
        when(userService.findById(999L)).thenThrow(new RuntimeException("Erro inesperado"));

        // Act & Assert
        mockMvc.perform(get("/users/999"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorDto com múltiplos erros de validação quando houver múltiplos campos inválidos")
    void shouldReturnErrorDtoWithMultipleValidationErrors_WhenMultipleFieldsAreInvalid() throws Exception {
        // Arrange
        UserRequestDto invalidUser = UserRequestDto.builder()
                .name("")
                .email("invalid-email")
                .cpf("123")
                .phone("123")
                .address("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors.name").value("O nome é obrigatório"))
                .andExpect(jsonPath("$.errors.email").value("Email inválido"))
                .andExpect(jsonPath("$.errors.cpf").value("CPF deve conter 11 dígitos numéricos"))
                .andExpect(jsonPath("$.errors.phone").value("Telefone deve conter 10 ou 11 dígitos numéricos"))
                .andExpect(jsonPath("$.errors.address").value("O endereço é obrigatório"));
    }
} 