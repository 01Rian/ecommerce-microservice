package com.ecommerce.shoppingapi.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import com.ecommerce.shoppingapi.services.ProductService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;

@DisplayName("Testes de Integração - Product Service")
class ProductServiceIntegrationTest extends BaseIntegrationTest {

    private static final String PRODUCT_IDENTIFIER = "prod-1";
    private static final String PRODUCT_NAME = "Produto Teste";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("100.00");
    private static final String NOT_FOUND_IDENTIFIER = "prod-not-found";
    private static final String ERROR_IDENTIFIER = "prod-error";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Produto não encontrado";

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar produto quando encontrado")
    void getProductByIdentifier_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        String responseBody = """
            {
                "id": 1,
                "name": "%s",
                "description": "Descrição do Produto",
                "price": %s,
                "quantity": 10,
                "productIdentifier": "%s",
                "categoryId": 1,
                "categoryName": "Categoria Teste",
                "createdAt": "01-03-2024 10:00:00",
                "updatedAt": "01-03-2024 10:00:00"
            }
            """.formatted(PRODUCT_NAME, PRODUCT_PRICE, PRODUCT_IDENTIFIER);

        wireMockServer.stubFor(get(urlEqualTo("/api/v1/products/" + PRODUCT_IDENTIFIER))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody(responseBody)));

        // Act
        ProductResponseDto produto = productService.getProductByIdentifier(PRODUCT_IDENTIFIER);

        // Assert
        assertAll(
            () -> assertNotNull(produto),
            () -> assertEquals(PRODUCT_IDENTIFIER, produto.getProductIdentifier()),
            () -> assertEquals(PRODUCT_NAME, produto.getName()),
            () -> assertEquals(PRODUCT_PRICE, produto.getPrice())
        );

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/products/" + PRODUCT_IDENTIFIER)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void getProductByIdentifier_WhenProductNotFound_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/products/" + NOT_FOUND_IDENTIFIER))
            .willReturn(aResponse()
                .withStatus(404)));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> productService.getProductByIdentifier(NOT_FOUND_IDENTIFIER)
        );

        assertEquals(PRODUCT_NOT_FOUND_MESSAGE, exception.getMessage());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/products/" + NOT_FOUND_IDENTIFIER)));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorrer erro na API")
    void getProductByIdentifier_WhenApiError_ShouldThrowException() {
        // Arrange
        wireMockServer.stubFor(get(urlEqualTo("/api/v1/products/" + ERROR_IDENTIFIER))
            .willReturn(aResponse()
                .withStatus(500)));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> productService.getProductByIdentifier(ERROR_IDENTIFIER)
        );

        assertEquals(PRODUCT_NOT_FOUND_MESSAGE, exception.getMessage());
        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/products/" + ERROR_IDENTIFIER)));
    }
}