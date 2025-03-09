package com.ecommerce.userapi.integration;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.domain.entity.User;
import com.ecommerce.userapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserApiApplicationTests {

    private TestRestTemplate restTemplate;
    private UserRepository userRepository;

    @Autowired
    public UserApiApplicationTests(TestRestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    private UserRequestDto userRequestDto;
    private User.UserBuilder userBuilder;

    @BeforeEach
    void setUp() {
        userRequestDto = UserRequestDto.builder()
                .name("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .phone("11999999999")
                .address("Rua Teste, 123")
                .build();

        userBuilder = User.builder()
                .name(userRequestDto.getName().toLowerCase())
                .cpf(userRequestDto.getCpf())
                .email(userRequestDto.getEmail())
                .phone(userRequestDto.getPhone())
                .address(userRequestDto.getAddress())
                .dataRegister(LocalDateTime.now());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Contexto da aplicação deve carregar com sucesso")
    void contextLoads() {
        assertThat(restTemplate).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void createUser_ShouldReturnCreatedUser_WhenSuccessful() {
        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
                "/users",
                userRequestDto,
                UserResponseDto.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(userRequestDto.getName().toLowerCase());
        assertThat(response.getBody().getCpf()).isEqualTo(userRequestDto.getCpf());
        assertThat(response.getBody().getEmail()).isEqualTo(userRequestDto.getEmail());
        assertThat(response.getBody().getPhone()).isEqualTo(userRequestDto.getPhone());
        assertThat(response.getBody().getAddress()).isEqualTo(userRequestDto.getAddress());
        assertThat(response.getBody().getDataRegister()).isNotNull();

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Não deve criar usuário com CPF duplicado")
    void createUser_ShouldReturnConflict_WhenCpfAlreadyExists() {
        // Arrange
        userRepository.save(userBuilder.build());

        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity(
                "/users",
                userRequestDto,
                UserResponseDto.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("Deve buscar todos os usuários com sucesso")
    void findAllUsers_ShouldReturnUserList_WhenSuccessful() {
        // Arrange
        User savedUser = userRepository.save(userBuilder.build());

        // Act
        ResponseEntity<List<UserResponseDto>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserResponseDto>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Deve buscar usuário por CPF com sucesso")
    void findUserByCpf_ShouldReturnUser_WhenSuccessful() {
        // Arrange
        User savedUser = userRepository.save(userBuilder.build());

        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
                "/users/cpf/{cpf}",
                UserResponseDto.class,
                savedUser.getCpf()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedUser.getId());
        assertThat(response.getBody().getCpf()).isEqualTo(savedUser.getCpf());
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void updateUser_ShouldReturnUpdatedUser_WhenSuccessful() {
        // Arrange
        User savedUser = userRepository.save(userBuilder.build());

        UserRequestDto updateRequest = UserRequestDto.builder()
                .name("João Silva Atualizado")
                .cpf(savedUser.getCpf())
                .email("joao.atualizado@email.com")
                .phone("11988888888")
                .address("Rua Atualizada, 456")
                .build();

        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.exchange(
                "/users/{cpf}",
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                UserResponseDto.class,
                savedUser.getCpf()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(updateRequest.getName().toLowerCase());
        assertThat(response.getBody().getEmail()).isEqualTo(updateRequest.getEmail());
        assertThat(response.getBody().getPhone()).isEqualTo(updateRequest.getPhone());
        assertThat(response.getBody().getAddress()).isEqualTo(updateRequest.getAddress());
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void deleteUser_ShouldRemoveUser_WhenSuccessful() {
        // Arrange
        User savedUser = userRepository.save(userBuilder.build());

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/users/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedUser.getId()
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar not found ao buscar usuário inexistente")
    void findNonExistingUser_ShouldReturnNotFound() {
        // Act
        ResponseEntity<UserResponseDto> response = restTemplate.getForEntity(
                "/users/cpf/{cpf}",
                UserResponseDto.class,
                "99999999999"
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}