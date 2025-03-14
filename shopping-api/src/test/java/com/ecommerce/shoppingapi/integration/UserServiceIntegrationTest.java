package com.ecommerce.shoppingapi.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import com.ecommerce.shoppingapi.services.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("Testes de Integração - User Service")
class UserServiceIntegrationTest extends BaseIntegrationTest {

    private static final String VALID_CPF = "12345678900";
    private static final String INVALID_CPF = "99999999999";
    private static final String ERROR_CPF = "88888888888";
    private static final String USER_NAME = "João da Silva";
    private static final String USER_EMAIL = "joao@email.com";
    private static final String USER_PHONE = "11999999999";
    private static final String USER_ADDRESS = "Rua Teste, 123";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuário não encontrado";

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Deve retornar usuário quando encontrado")
    void getUserByCpf_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String responseBody = """
            {
                "id": 1,
                "name": "%s",
                "cpf": "%s",
                "email": "%s",
                "phone": "%s",
                "address": "%s",
                "dataRegister": "01-03-2024 10:00:00"
            }
            """.formatted(USER_NAME, VALID_CPF, USER_EMAIL, USER_PHONE, USER_ADDRESS);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + VALID_CPF))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)));

        // Act
        UserResponseDto usuario = userService.getUserByCpf(VALID_CPF);

        // Assert
        assertAll(
            () -> assertNotNull(usuario),
            () -> assertEquals(USER_NAME, usuario.getName()),
            () -> assertEquals(VALID_CPF, usuario.getCpf()),
            () -> assertEquals(USER_EMAIL, usuario.getEmail()),
            () -> assertEquals(USER_PHONE, usuario.getPhone()),
            () -> assertEquals(USER_ADDRESS, usuario.getAddress())
        );

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + VALID_CPF)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void getUserByCpf_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + INVALID_CPF))
            .willReturn(aResponse()
                .withStatus(404)));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getUserByCpf(INVALID_CPF)
        );

        assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + INVALID_CPF)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro na API")
    void getUserByCpf_WhenApiError_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + ERROR_CPF))
            .willReturn(aResponse()
                .withStatus(500)));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getUserByCpf(ERROR_CPF)
        );

        assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + ERROR_CPF)));
    }
}