package com.ecommerce.productapi.integration;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.domain.entities.Product;
import com.ecommerce.productapi.repositories.CategoryRepository;
import com.ecommerce.productapi.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Integração - Product API")
class ProductIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URI = "/products";
    private static final String PRODUCT_NAME = "Smartphone";
    private static final String PRODUCT_DESCRIPTION = "Smartphone último modelo";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("1999.99");
    private static final Integer PRODUCT_QUANTITY = 10;
    private static final String PRODUCT_IDENTIFIER = "SMART123";
    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String UPDATED_NAME = "Smartphone Atualizado";
    private static final String UPDATED_DESCRIPTION = "Descrição atualizada";
    private static final BigDecimal UPDATED_PRICE = new BigDecimal("2499.99");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;
    private ProductRequest productRequest;
    
    @BeforeEach
    void setUp() {
        // Criando categoria para os testes
        category = categoryRepository.save(Category.builder()
                .name(CATEGORY_NAME)
                .description("Produtos eletrônicos em geral")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        // Preparando objetos para os testes
        productRequest = ProductRequest.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .categoryId(category.getId())
                .build();

        product = Product.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .productIdentifier(PRODUCT_IDENTIFIER)
                .category(category)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ProductResponse.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de Criação de Produto")
    class CreateProductTests {

        @Test
        @DisplayName("Deve criar um produto com sucesso")
        void createProduct_WithValidData_ReturnsCreated() {
            // Act
            ResponseEntity<ProductResponse> response = restTemplate.postForEntity(
                    BASE_URI,
                    productRequest,
                    ProductResponse.class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).satisfies(created -> {
                assertThat(created.getId()).isNotNull();
                assertThat(created.getName()).isEqualTo(PRODUCT_NAME);
                assertThat(created.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                assertThat(created.getPrice()).isEqualTo(PRODUCT_PRICE);
                assertThat(created.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
                assertThat(created.getProductIdentifier()).isNotNull();
                assertThat(created.getCategoryId()).isEqualTo(category.getId());
                assertThat(created.getCategoryName()).isEqualTo(CATEGORY_NAME);
            });
        }

        @Test
        @DisplayName("Deve retornar erro ao criar produto com categoria inexistente")
        void createProduct_WithInvalidCategory_ReturnsBadRequest() {
            // Arrange
            productRequest.setCategoryId(999L);

            // Act
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URI,
                    productRequest,
                    String.class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Produto")
    class FindProductTests {

        @Test
        @DisplayName("Deve retornar todos os produtos")
        void findAllProducts_ReturnsAllProducts() {
            // Arrange
            product = productRepository.save(product);

            // Act
            ResponseEntity<ProductResponse[]> response = restTemplate.getForEntity(
                    BASE_URI,
                    ProductResponse[].class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody()[0]).satisfies(found -> {
                assertThat(found.getName()).isEqualTo(PRODUCT_NAME);
                assertThat(found.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                assertThat(found.getPrice()).isEqualTo(PRODUCT_PRICE);
                assertThat(found.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
            });
        }

        @Test
        @DisplayName("Deve retornar um produto por identificador")
        void findProductByIdentifier_WithValidIdentifier_ReturnsProduct() {
            // Arrange
            product = productRepository.save(product);

            // Act
            ResponseEntity<ProductResponse> response = restTemplate.getForEntity(
                    BASE_URI + "/{identifier}",
                    ProductResponse.class,
                    product.getProductIdentifier()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).satisfies(found -> {
                assertThat(found.getProductIdentifier()).isEqualTo(product.getProductIdentifier());
                assertThat(found.getName()).isEqualTo(PRODUCT_NAME);
                assertThat(found.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                assertThat(found.getPrice()).isEqualTo(PRODUCT_PRICE);
                assertThat(found.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
            });
        }

        @Test
        @DisplayName("Deve retornar erro ao buscar produto com identificador inexistente")
        void findProductByIdentifier_WithInvalidIdentifier_ReturnsNotFound() {
            // Act
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URI + "/{identifier}",
                    String.class,
                    "INVALID123"
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Produto")
    class UpdateProductTests {

        @Test
        @DisplayName("Deve atualizar um produto com sucesso")
        void updateProduct_WithValidData_ReturnsUpdatedProduct() {
            // Arrange
            product = productRepository.save(product);
            ProductRequest updateRequest = ProductRequest.builder()
                    .name(UPDATED_NAME)
                    .description(UPDATED_DESCRIPTION)
                    .price(UPDATED_PRICE)
                    .quantity(PRODUCT_QUANTITY)
                    .categoryId(category.getId())
                    .build();

            // Act
            ResponseEntity<ProductResponse> response = restTemplate.exchange(
                    BASE_URI + "/{identifier}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    ProductResponse.class,
                    product.getProductIdentifier()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).satisfies(updated -> {
                assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
                assertThat(updated.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(updated.getPrice()).isEqualTo(UPDATED_PRICE);
            });
        }
    }

    @Nested
    @DisplayName("Testes de Deleção de Produto")
    class DeleteProductTests {

        @Test
        @DisplayName("Deve deletar um produto com sucesso")
        void deleteProduct_WithValidId_ReturnsNoContent() {
            // Arrange
            product = productRepository.save(product);

            // Act
            ResponseEntity<Void> response = restTemplate.exchange(
                    BASE_URI + "/{id}",
                    HttpMethod.DELETE,
                    null,
                    Void.class,
                    product.getId()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(productRepository.findById(product.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar erro ao tentar deletar produto inexistente")
        void deleteProduct_WithInvalidId_ReturnsNotFound() {
            // Act
            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URI + "/{id}",
                    HttpMethod.DELETE,
                    null,
                    String.class,
                    999L
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}