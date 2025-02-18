package com.ecommerce.productapi.services;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.domain.entities.Product;
import com.ecommerce.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.exception.ProductNotFoundException;
import com.ecommerce.productapi.mappers.impl.ProductMapper;
import com.ecommerce.productapi.repositories.CategoryRepository;
import com.ecommerce.productapi.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Testes do ProductService")
class ProductServiceTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final String VALID_IDENTIFIER = "123e4567-e89b-12d3-a456-426614174000";
    private static final String INVALID_IDENTIFIER = "non-existent";
    private static final String PRODUCT_NAME = "Smartphone";
    private static final String PRODUCT_DESCRIPTION = "Um smartphone muito legal";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("1000.00");
    private static final int PRODUCT_QUANTITY = 10;
    private static final String CATEGORY_NAME = "Eletrônicos";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private Category category;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        category = createCategory();
        product = createProduct();
        productRequest = createProductRequest();
        productResponse = createProductResponse();
    }

    @Nested
    @DisplayName("Testes de busca de produtos")
    class FindProductsTests {

        @Test
        @DisplayName("Deve retornar lista de produtos quando existirem registros")
        void shouldReturnProductList_WhenProductsExist() {
            // Arrange
            when(productRepository.findAll()).thenReturn(List.of(product));
            when(mapper.toResponse(any(Product.class))).thenReturn(productResponse);

            // Act
            List<ProductResponse> result = productService.findAllProducts();

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .first()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                        assertThat(response.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
                    });

            verify(productRepository).findAll();
            verify(mapper).toResponse(any(Product.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem produtos")
        void shouldReturnEmptyList_WhenNoProductsExist() {
            // Arrange
            when(productRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<ProductResponse> result = productService.findAllProducts();

            // Assert
            assertThat(result).isEmpty();
            verify(productRepository).findAll();
            verify(mapper, never()).toResponse(any(Product.class));
        }

        @Test
        @DisplayName("Deve retornar página de produtos com sucesso")
        void shouldReturnPageOfProducts_Successfully() {
            // Arrange
            PageRequest pageRequest = PageRequest.of(0, 1);
            Page<Product> productPage = new PageImpl<>(List.of(product));
            when(productRepository.findAll(pageRequest)).thenReturn(productPage);
            when(mapper.toResponse(any(Product.class))).thenReturn(productResponse);

            // Act
            Page<ProductResponse> result = productService.findAllPageProducts(pageRequest);

            // Assert
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getNumber()).isZero();
            assertThat(result.getSize()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getContent())
                    .hasSize(1)
                    .first()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                    });

            verify(productRepository).findAll(pageRequest);
            verify(mapper).toResponse(any(Product.class));
        }

        @Test
        @DisplayName("Deve retornar produtos por categoria quando existirem")
        void shouldReturnProducts_WhenCategoryExists() {
            // Arrange
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(productRepository.getProductByCategory(category.getId())).thenReturn(List.of(product));
            when(mapper.toResponse(any(Product.class))).thenReturn(productResponse);

            // Act
            List<ProductResponse> result = productService.findProductByCategoryId(category.getId());

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .first()
                    .satisfies(response -> {
                        assertThat(response.getCategoryId()).isEqualTo(category.getId());
                        assertThat(response.getCategoryName()).isEqualTo(category.getName());
                    });

            verify(productRepository).getProductByCategory(category.getId());
            verify(mapper).toResponse(any(Product.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem produtos por categoria")
        void shouldReturnEmptyList_WhenNoProductsByCategoryExist() {
            // Arrange
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(productRepository.getProductByCategory(category.getId())).thenReturn(Collections.emptyList());

            // Act
            List<ProductResponse> result = productService.findProductByCategoryId(category.getId());

            // Assert
            assertThat(result).isEmpty();
            verify(productRepository).getProductByCategory(category.getId());
            verify(mapper, never()).toResponse(any(Product.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar produtos por categoria inexistente")
        void shouldThrowException_WhenCategoryDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> productService.findProductByCategoryId(INVALID_ID))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessageContaining(String.valueOf(INVALID_ID));

            verify(categoryRepository).findById(INVALID_ID);
            verify(productRepository, never()).getProductByCategory(any());
        }

        @Test
        @DisplayName("Deve retornar produto por identifier quando existir")
        void shouldReturnProduct_WhenIdentifierExists() {
            // Arrange
            when(productRepository.findByProductIdentifier(VALID_IDENTIFIER)).thenReturn(product);
            when(mapper.toResponse(product)).thenReturn(productResponse);

            // Act
            ProductResponse result = productService.findByProductIdentifier(VALID_IDENTIFIER);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getProductIdentifier()).isEqualTo(VALID_IDENTIFIER);
                        assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                    });

            verify(productRepository).findByProductIdentifier(VALID_IDENTIFIER);
            verify(mapper).toResponse(product);
        }

        @Test
        @DisplayName("Deve lançar exceção quando identifier não existir")
        void shouldThrowException_WhenIdentifierDoesNotExist() {
            // Arrange
            when(productRepository.findByProductIdentifier(INVALID_IDENTIFIER)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> productService.findByProductIdentifier(INVALID_IDENTIFIER))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining(INVALID_IDENTIFIER);

            verify(productRepository).findByProductIdentifier(INVALID_IDENTIFIER);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Testes de operações de persistência")
    class PersistenceOperationsTests {

        @Test
        @DisplayName("Deve salvar produto com sucesso")
        void shouldSaveProduct_Successfully() {
            // Arrange
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(mapper.toEntity(productRequest)).thenReturn(product);
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(mapper.toResponse(product)).thenReturn(productResponse);

            // Act
            ProductResponse result = productService.save(productRequest);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                        assertThat(response.getPrice()).isEqualByComparingTo(PRODUCT_PRICE);
                        assertThat(response.getCategoryId()).isEqualTo(category.getId());
                    });

            verify(categoryRepository).findById(category.getId());
            verify(productRepository).save(any(Product.class));
            verify(mapper).toResponse(product);
        }

        @Test
        @DisplayName("Deve lançar exceção ao salvar produto com categoria inexistente")
        void shouldThrowException_WhenSavingWithInvalidCategory() {
            // Arrange
            when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
            ProductRequest invalidRequest = createProductRequest();
            invalidRequest.setCategoryId(INVALID_ID);

            // Act & Assert
            assertThatThrownBy(() -> productService.save(invalidRequest))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessageContaining(String.valueOf(INVALID_ID));

            verify(categoryRepository).findById(INVALID_ID);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve atualizar produto com sucesso")
        void shouldUpdateProduct_Successfully() {
            // Arrange
            when(productRepository.findByProductIdentifier(VALID_IDENTIFIER)).thenReturn(product);
            when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(mapper.toResponse(product)).thenReturn(productResponse);

            // Act
            ProductResponse result = productService.update(VALID_IDENTIFIER, productRequest);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getName()).isEqualTo(PRODUCT_NAME);
                        assertThat(response.getPrice()).isEqualByComparingTo(PRODUCT_PRICE);
                        assertThat(response.getCategoryId()).isEqualTo(category.getId());
                    });

            verify(productRepository).findByProductIdentifier(VALID_IDENTIFIER);
            verify(categoryRepository).findById(category.getId());
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
        void shouldThrowException_WhenUpdatingNonExistentProduct() {
            // Arrange
            when(productRepository.findByProductIdentifier(INVALID_IDENTIFIER)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> productService.update(INVALID_IDENTIFIER, productRequest))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining(INVALID_IDENTIFIER);

            verify(productRepository).findByProductIdentifier(INVALID_IDENTIFIER);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve deletar produto com sucesso")
        void shouldDeleteProduct_Successfully() {
            // Arrange
            when(productRepository.existsById(VALID_ID)).thenReturn(true);

            // Act
            productService.delete(VALID_ID);

            // Assert
            verify(productRepository).existsById(VALID_ID);
            verify(productRepository).deleteById(VALID_ID);
        }

        @Test
        @DisplayName("Deve lançar exceção ao deletar produto inexistente")
        void shouldThrowException_WhenDeletingNonExistentProduct() {
            // Arrange
            when(productRepository.existsById(INVALID_ID)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> productService.delete(INVALID_ID))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining(String.valueOf(INVALID_ID));

            verify(productRepository).existsById(INVALID_ID);
            verify(productRepository, never()).deleteById(any());
        }
    }

    private Category createCategory() {
        return Category.builder()
                .id(VALID_ID)
                .name(CATEGORY_NAME)
                .build();
    }

    private Product createProduct() {
        return Product.builder()
                .id(VALID_ID)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .productIdentifier(VALID_IDENTIFIER)
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private ProductRequest createProductRequest() {
        return ProductRequest.builder()
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .categoryId(VALID_ID)
                .build();
    }

    private ProductResponse createProductResponse() {
        return ProductResponse.builder()
                .id(VALID_ID)
                .name(PRODUCT_NAME)
                .description(PRODUCT_DESCRIPTION)
                .price(PRODUCT_PRICE)
                .quantity(PRODUCT_QUANTITY)
                .productIdentifier(VALID_IDENTIFIER)
                .categoryId(VALID_ID)
                .categoryName(CATEGORY_NAME)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}