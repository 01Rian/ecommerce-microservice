package com.ecommerce.shoppingapi.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ecommerce.shoppingapi.domain.dto.shop.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.services.ShopService;

@SpringBootTest
@DisplayName("Testes de Integração - Shop Service")
class ShopServiceIntegrationTest extends BaseIntegrationTest {

    private static final String VALID_USER_CPF = "12345678900";
    private static final String INVALID_USER_CPF = "99999999999";
    private static final String VALID_PRODUCT_IDENTIFIER = "prod-1";
    private static final String INVALID_PRODUCT_IDENTIFIER = "prod-not-found";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("100.00");
    private static final String USER_NAME = "João da Silva";
    private static final String USER_EMAIL = "joao@email.com";

    @Autowired
    private ShopService shopService;

    @Test
    @DisplayName("Deve salvar uma compra com sucesso")
    void save_WhenValidRequest_ShouldCreateShop() {
        // Arrange
        String userResponseBody = """
            {
                "name": "%s",
                "cpf": "%s",
                "email": "%s",
                "phone": "11999999999",
                "address": "Rua Teste, 123",
                "dataRegister": "01-03-2024 10:00:00"
            }
            """.formatted(USER_NAME, VALID_USER_CPF, USER_EMAIL);

        String productResponseBody = """
            {
                "id": 1,
                "name": "Produto Teste",
                "description": "Descrição do Produto",
                "price": %s,
                "quantity": 10,
                "productIdentifier": "%s",
                "categoryId": 1,
                "categoryName": "Categoria Teste",
                "createdAt": "01-03-2024 10:00:00",
                "updatedAt": "01-03-2024 10:00:00"
            }
            """.formatted(PRODUCT_PRICE, VALID_PRODUCT_IDENTIFIER);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(userResponseBody)));

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/products/" + VALID_PRODUCT_IDENTIFIER))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(productResponseBody)));

        ShopRequestDto request = ShopRequestDto.builder()
            .userIdentifier(VALID_USER_CPF)
            .items(Arrays.asList(
                ItemDto.builder()
                    .productIdentifier(VALID_PRODUCT_IDENTIFIER)
                    .build()
            ))
            .build();

        // Act
        ShopResponseDto response = shopService.save(request);

        // Assert
        assertAll(
            () -> assertNotNull(response),
            () -> assertNotNull(response.getId()),
            () -> assertEquals(VALID_USER_CPF, response.getUserIdentifier()),
            () -> assertEquals(PRODUCT_PRICE, response.getTotal()),
            () -> assertNotNull(response.getDate()),
            () -> assertEquals(1, response.getItems().size()),
            () -> assertEquals(VALID_PRODUCT_IDENTIFIER, response.getItems().get(0).getProductIdentifier()),
            () -> assertEquals(PRODUCT_PRICE, response.getItems().get(0).getPrice())
        );

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF)));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/products/" + VALID_PRODUCT_IDENTIFIER)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao salvar compra")
    void save_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + INVALID_USER_CPF))
            .willReturn(aResponse()
                .withStatus(404)));

        ShopRequestDto request = ShopRequestDto.builder()
            .userIdentifier(INVALID_USER_CPF)
            .items(Arrays.asList(
                ItemDto.builder()
                    .productIdentifier(VALID_PRODUCT_IDENTIFIER)
                    .build()
            ))
            .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            shopService.save(request)
        );

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + INVALID_USER_CPF)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void save_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        String userResponseBody = """
            {
                "name": "%s",
                "cpf": "%s",
                "email": "%s",
                "phone": "11999999999",
                "address": "Rua Teste, 123",
                "dataRegister": "01-03-2024 10:00:00"
            }
            """.formatted(USER_NAME, VALID_USER_CPF, USER_EMAIL);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(userResponseBody)));

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/products/" + INVALID_PRODUCT_IDENTIFIER))
            .willReturn(aResponse()
                .withStatus(404)));

        ShopRequestDto request = ShopRequestDto.builder()
            .userIdentifier(VALID_USER_CPF)
            .items(Arrays.asList(
                ItemDto.builder()
                    .productIdentifier(INVALID_PRODUCT_IDENTIFIER)
                    .build()
            ))
            .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            shopService.save(request)
        );

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF)));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/products/" + INVALID_PRODUCT_IDENTIFIER)));
    }

    @Test
    @DisplayName("Deve retornar compras por usuário")
    void getByUser_WhenUserExists_ShouldReturnShops() {
        // Arrange
        String userResponseBody = """
            {
                "name": "%s",
                "cpf": "%s",
                "email": "%s",
                "phone": "11999999999",
                "address": "Rua Teste, 123",
                "dataRegister": "01-03-2024 10:00:00"
            }
            """.formatted(USER_NAME, VALID_USER_CPF, USER_EMAIL);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(userResponseBody)));

        // Act
        List<ShopResponseDto> shops = shopService.getByUser(VALID_USER_CPF);

        // Assert
        assertNotNull(shops);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + VALID_USER_CPF)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado ao buscar compras")
    void getByUser_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/users/cpf/" + INVALID_USER_CPF))
            .willReturn(aResponse()
                .withStatus(404)));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            shopService.getByUser(INVALID_USER_CPF)
        );
        
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/users/cpf/" + INVALID_USER_CPF)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar compra inexistente")
    void delete_WhenShopNotFound_ShouldThrowException() {
        // Act & Assert
        assertThrows(ShoppingNotFoundException.class, () -> 
            shopService.delete(999L)
        );
    }

    @Test
    @DisplayName("Deve buscar compras por filtro")
    void getShopsByFilter_ShouldReturnFilteredShops() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        BigDecimal maxValue = new BigDecimal("1000.00");

        // Act
        List<ShopResponseDto> shops = shopService.getShopsByFilter(startDate, endDate, maxValue);

        // Assert
        assertNotNull(shops);
    }
}