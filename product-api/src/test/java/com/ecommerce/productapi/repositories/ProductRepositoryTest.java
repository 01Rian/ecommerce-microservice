package com.ecommerce.productapi.repositories;

import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.domain.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do ProductRepository")
class ProductRepositoryTest {

    private static final String VALID_IDENTIFIER = "123e4567-e89b-12d3-a456-426614174000";
    private static final String INVALID_IDENTIFIER = "non-existent-identifier";
    private static final Long INVALID_CATEGORY_ID = 999L;

    private final ProductRepository productRepository;
    private final TestEntityManager entityManager;

    private Category category;
    private Product product;

    @Autowired
    public ProductRepositoryTest(ProductRepository productRepository, TestEntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        category = createAndPersistCategory();
        product = createAndPersistProduct(category);
    }

    @Nested
    @DisplayName("Testes de busca por identifier")
    class FindByProductIdentifierTests {
        
        @Test
        @DisplayName("Deve retornar produto quando buscar por identifier válido")
        void shouldReturnProduct_WhenSearchingByValidIdentifier() {
            // Act
            Product result = productRepository.findByProductIdentifier(VALID_IDENTIFIER);

            // Assert
            assertThat(result).isNotNull()
                    .satisfies(p -> {
                        assertThat(p.getProductIdentifier()).isEqualTo(VALID_IDENTIFIER);
                        assertThat(p.getName()).isEqualTo("Smartphone");
                        assertThat(p.getDescription()).isEqualTo("Um smartphone muito legal");
                        assertThat(p.getPrice()).isEqualByComparingTo(new BigDecimal("1000.00"));
                        assertThat(p.getQuantity()).isEqualTo(10);
                        assertThat(p.getCategory()).isEqualTo(category);
                    });
        }

        @Test
        @DisplayName("Deve retornar null quando buscar por identifier inválido")
        void shouldReturnNull_WhenSearchingByInvalidIdentifier() {
            // Act
            Product result = productRepository.findByProductIdentifier(INVALID_IDENTIFIER);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de busca por categoria")
    class FindByCategoryTests {

        @Test
        @DisplayName("Deve retornar lista de produtos quando categoria existe")
        void shouldReturnProductList_WhenCategoryExists() {

            // Arrange
            createAndPersistProduct(category, "Tablet", "223e4567-e89b-12d3-a456-426614174000"); // Outro produto

            // Act
            List<Product> result = productRepository.getProductByCategory(category.getId());

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .allSatisfy(p -> {
                        assertThat(p.getCategory().getId()).isEqualTo(category.getId());
                        assertThat(p.getName()).isIn("Smartphone", "Tablet");
                    });
                    
            assertThat(result)
                    .extracting(Product::getName)
                    .containsExactlyInAnyOrder("Smartphone", "Tablet");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando categoria não existe")
        void shouldReturnEmptyList_WhenCategoryDoesNotExist() {
            // Act
            List<Product> result = productRepository.getProductByCategory(INVALID_CATEGORY_ID);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de persistência")
    class PersistenceTests {

        @Test
        @DisplayName("Deve salvar produto com sucesso")
        void shouldSaveProduct_Successfully() {
            // Arrange
            Product newProduct = createProductEntity(category, "Notebook", "323e4567-e89b-12d3-a456-426614174000");

            // Act
            Product savedProduct = productRepository.save(newProduct);

            // Assert
            assertThat(savedProduct)
                    .isNotNull()
                    .satisfies(p -> {
                        assertThat(p.getId()).isNotNull();
                        assertThat(p.getName()).isEqualTo("Notebook");
                        assertThat(p.getProductIdentifier()).isEqualTo("323e4567-e89b-12d3-a456-426614174000");
                    });

            Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
            assertThat(foundProduct)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(savedProduct);
        }

        @Test
        @DisplayName("Deve deletar produto com sucesso")
        void shouldDeleteProduct_Successfully() {
            // Arrange
            Long productId = product.getId();

            // Act
            productRepository.deleteById(productId);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Product foundProduct = entityManager.find(Product.class, productId);
            assertThat(foundProduct).isNull();
        }
    }

    private Category createAndPersistCategory() {
        Category newCategory = Category.builder()
                .name("Eletrônicos")
                .description("Categoria de produtos eletrônicos")
                .build();
        return entityManager.persist(newCategory);
    }

    private Product createAndPersistProduct(Category category) {
        return createAndPersistProduct(category, "Smartphone", VALID_IDENTIFIER);
    }

    private Product createAndPersistProduct(Category category, String name, String identifier) {
        Product newProduct = createProductEntity(category, name, identifier);
        entityManager.persist(newProduct);
        entityManager.flush();
        return newProduct;
    }

    private Product createProductEntity(Category category, String name, String identifier) {
        return Product.builder()
                .name(name)
                .description("Um " + name.toLowerCase() + " muito legal")
                .price(new BigDecimal("1000.00"))
                .quantity(10)
                .productIdentifier(identifier)
                .category(category)
                .build();
    }
}