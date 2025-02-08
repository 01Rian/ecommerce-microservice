package com.ecommerce.userapi.controller;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.exception.UserAlreadyExistsException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import com.ecommerce.userapi.exception.advice.GlobalExceptionHandler;
import com.ecommerce.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {UserController.class, GlobalExceptionHandler.class})
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @Autowired
    UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(1L)
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .dataRegister(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve retornar lista de usuários com sucesso")
    void findAll_ShouldReturnListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    @DisplayName("Deve retornar página de usuários com sucesso")
    void findByPage_ShouldReturnPageOfUsers() throws Exception {
        Page<UserResponseDto> page = new PageImpl<>(List.of(userResponseDto));
        when(userService.findByPage(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/users/pageable")
                        .param("page", "0")
                        .param("linesPerPage", "12")
                        .param("direction", "ASC")
                        .param("orderBy", "name"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar usuário por ID com sucesso")
    void findById_ShouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve retornar usuário por CPF com sucesso")
    void findByCpf_ShouldReturnUser() throws Exception {
        when(userService.findByCpf("12345678901")).thenReturn(userResponseDto);

        mockMvc.perform(get("/users/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("Deve retornar usuários por nome com sucesso")
    void findByQueryName_ShouldReturnUsers() throws Exception {
        when(userService.findByQueryName(anyString())).thenReturn(List.of(userResponseDto));

        mockMvc.perform(get("/users/search")
                        .param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDto.class))).thenReturn(userResponseDto);

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(userResponseDto.getName()))
                .andExpect(jsonPath("$.cpf").value(userResponseDto.getCpf()))
                .andExpect(jsonPath("$.email").value(userResponseDto.getEmail()))
                .andExpect(jsonPath("$.phone").value(userResponseDto.getPhone()))
                .andExpect(jsonPath("$.address").value(userResponseDto.getAddress()));

        // Verify
        verify(userService, times(1)).save(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar criar usuário com dados inválidos")
    void createUser_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        // Arrange
        UserRequestDto invalidUser = UserRequestDto.builder()
                .name("")
                .cpf("123")  // CPF inválido
                .email("email-invalido")  // Email inválido
                .phone("123")  // Telefone inválido
                .address("")  // Endereço vazio
                .build();

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").exists());

        // Verify
        verify(userService, never()).save(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao tentar criar usuário com payload vazio")
    void createUser_ShouldReturnBadRequest_WhenEmptyPayload() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").exists());

        // Verify
        verify(userService, never()).save(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("Deve retornar erro 415 ao tentar criar usuário com Content-Type inválido")
    void createUser_ShouldReturnUnsupportedMediaType_WhenInvalidContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(userRequestDto.toString()))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.message").value("Tipo de conteúdo não suportado. Use application/json"))
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.timestamp").exists());

        // Verify
        verify(userService, never()).save(any(UserRequestDto.class));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.update(any(UserRequestDto.class), anyString())).thenReturn(userResponseDto);

        mockMvc.perform(put("/users/12345678901")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando buscar usuário por ID inexistente")
    void findById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 999L;
        when(userService.findById(userId))
                .thenThrow(new UserNotFoundException("id", userId));

        // Act & Assert
        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com id: '999'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando buscar usuário por CPF inexistente")
    void findByCpf_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        String cpf = "99999999999";
        when(userService.findByCpf(cpf))
                .thenThrow(new UserNotFoundException("cpf", cpf));

        // Act & Assert
        mockMvc.perform(get("/users/cpf/" + cpf))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com cpf: '99999999999'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 409 quando tentar criar usuário com CPF já existente")
    void createUser_ShouldReturnConflict_WhenCpfAlreadyExists() throws Exception {
        // Arrange
        when(userService.save(any(UserRequestDto.class)))
                .thenThrow(new UserAlreadyExistsException("cpf", userRequestDto.getCpf()));

        // Act & Assert
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Usuário já existe com cpf: '12345678901'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando tentar atualizar usuário inexistente")
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        String cpf = "99999999999";
        when(userService.update(any(UserRequestDto.class), eq(cpf)))
                .thenThrow(new UserNotFoundException("cpf", cpf));

        // Act & Assert
        mockMvc.perform(put("/users/" + cpf)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com cpf: '99999999999'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando tentar deletar usuário inexistente")
    void deleteUser_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 999L;
        doThrow(new UserNotFoundException("id", userId))
                .when(userService).delete(userId);

        // Act & Assert
        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado com id: '999'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer uma exceção não tratada")
    void shouldReturnInternalServerError_WhenUnexpectedErrorOccurs() throws Exception {
        // Arrange
        when(userService.findAll())
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Act & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
} 