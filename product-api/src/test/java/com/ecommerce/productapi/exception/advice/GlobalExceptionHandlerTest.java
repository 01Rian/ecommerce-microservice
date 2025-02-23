package com.ecommerce.productapi.exception.advice;

import com.ecommerce.productapi.controllers.ProductController;
import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.exception.ProductNotFoundException;
import com.ecommerce.productapi.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProductController.class, GlobalExceptionHandler.class})
class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public GlobalExceptionHandlerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("deve retornar ErrorResponse correto quando ProductNotFoundException for lançada")
    void shouldReturnCorrectErrorResponse_WhenProductNotFoundExceptionIsThrown() throws Exception {
        // Arrange
        String identifier = "product-123";
        when(productService.findByProductIdentifier(identifier))
                .thenThrow(new ProductNotFoundException("identifier", identifier));

        // Act & Assert
        mockMvc.perform(get("/products/" + identifier))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto não encontrado com identifier: '" + identifier + "'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorResponse correto quando CategoryNotFoundException for lançada")
    void shouldReturnCorrectErrorResponse_WhenCategoryNotFoundExceptionIsThrown() throws Exception {
        // Arrange
        Long categoryId = 1L;
        when(productService.findProductByCategoryId(categoryId))
                .thenThrow(new CategoryNotFoundException("id", categoryId));

        // Act & Assert
        mockMvc.perform(get("/products/category/" + categoryId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoria não encontrado com id: '" + categoryId + "'"))
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorResponse correto quando MethodArgumentNotValidException for lançada")
    void shouldReturnCorrectErrorResponse_WhenMethodArgumentNotValidExceptionIsThrown() throws Exception {
        // Arrange
        ProductRequest invalidProduct = ProductRequest.builder()
                .name("")
                .description("")
                .price(BigDecimal.ZERO)
                .build();

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorResponse correto quando HttpMediaTypeNotSupportedException for lançada")
    void shouldReturnCorrectErrorResponse_WhenHttpMediaTypeNotSupportedExceptionIsThrown() throws Exception {
        // Arrange
        ProductRequest validProduct = ProductRequest.builder()
                .name("Produto Test")
                .description("Descrição teste")
                .price(BigDecimal.TEN)
                .categoryId(1L)
                .build();

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(validProduct)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.message").value("Tipo de conteúdo não suportado. Use application/json"))
                .andExpect(jsonPath("$.errorCode").value("UNSUPPORTED_MEDIA_TYPE"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("deve retornar ErrorResponse correto quando Exception genérica for lançada")
    void shouldReturnCorrectErrorResponse_WhenGenericExceptionIsThrown() throws Exception {
        // Arrange
        String identifier = "invalid-id";
        when(productService.findByProductIdentifier(identifier))
                .thenThrow(new RuntimeException("Erro inesperado"));

        // Act & Assert
        mockMvc.perform(get("/products/" + identifier))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor: Erro inesperado"))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
