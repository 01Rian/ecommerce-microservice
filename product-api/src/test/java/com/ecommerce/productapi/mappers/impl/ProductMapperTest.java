package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.domain.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "Smartphone";
    private static final String PRODUCT_DESCRIPTION = "Smartphone última geração";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("1999.99");
    private static final Integer PRODUCT_QUANTITY = 10;
    private static final String PRODUCT_IDENTIFIER = "SMART123";
    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Eletrônicos";

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductMapper productMapper;

    private LocalDateTime now;
    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private Category category;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        category = Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .build();

        product = Product.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .productIdentifier(PRODUCT_IDENTIFIER)
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .build();

        productRequest = ProductRequest.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .categoryId(CATEGORY_ID)
                .build();

        productResponse = ProductResponse.builder()
                .id(PRODUCT_ID)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .productIdentifier(PRODUCT_IDENTIFIER)
                .categoryId(CATEGORY_ID)
                .categoryName(CATEGORY_NAME)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("Testes de conversão para Response")
    class ToResponseTests {

        @Test
        @DisplayName("Deve converter Product para ProductResponse corretamente")
        void toResponse_WithValidProduct_ReturnsProductResponse() {
            // Arrange
            when(modelMapper.map(product, ProductResponse.class)).thenReturn(productResponse);

            // Act
            ProductResponse result = productMapper.toResponse(product);

            // Assert
            assertThat(result).satisfies(response -> {
                assertThat(response.getId()).isEqualTo(PRODUCT_ID);
                assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                assertThat(response.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                assertThat(response.getPrice()).isEqualTo(PRODUCT_PRICE);
                assertThat(response.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
                assertThat(response.getProductIdentifier()).isEqualTo(PRODUCT_IDENTIFIER);
                assertThat(response.getCategoryId()).isEqualTo(CATEGORY_ID);
                assertThat(response.getCategoryName()).isEqualTo(CATEGORY_NAME);
                assertThat(response.getCreatedAt()).isEqualTo(now);
                assertThat(response.getUpdatedAt()).isEqualTo(now);
            });
        }

        @Test
        @DisplayName("Deve retornar null quando Product for null")
        void toResponse_WithNullProduct_ReturnsNull() {
            // Arrange
            when(modelMapper.map(null, ProductResponse.class)).thenReturn(null);

            // Act
            ProductResponse result = productMapper.toResponse(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de conversão para Entity")
    class ToEntityTests {

        @Test
        @DisplayName("Deve converter ProductRequest para Product corretamente")
        void toEntity_WithValidRequest_ReturnsProduct() {
            // Arrange
            Product expectedProduct = Product.builder()
                    .name(PRODUCT_NAME)
                    .description(PRODUCT_DESCRIPTION)
                    .price(PRODUCT_PRICE)
                    .quantity(PRODUCT_QUANTITY)
                    .build();

            when(modelMapper.map(productRequest, Product.class)).thenReturn(expectedProduct);

            // Act
            Product result = productMapper.toEntity(productRequest);

            // Assert
            assertThat(result).satisfies(entity -> {
                assertThat(entity.getName()).isEqualTo(PRODUCT_NAME);
                assertThat(entity.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                assertThat(entity.getPrice()).isEqualTo(PRODUCT_PRICE);
                assertThat(entity.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
            });
        }

        @Test
        @DisplayName("Deve retornar null quando ProductRequest for null")
        void toEntity_WithNullRequest_ReturnsNull() {
            // Arrange
            when(modelMapper.map(null, Product.class)).thenReturn(null);

            // Act
            Product result = productMapper.toEntity(null);

            // Assert
            assertThat(result).isNull();
        }
    }
}