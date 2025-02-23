package com.ecommerce.productapi.integration;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.repositories.CategoryRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Integração - Category API")
class CategoryIntegrationTest extends BaseIntegrationTest {

    private static final String BASE_URI = "/categories";
    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String CATEGORY_DESCRIPTION = "Produtos eletrônicos em geral";
    private static final String UPDATED_NAME = "Eletrônicos Atualizados";
    private static final String UPDATED_DESCRIPTION = "Descrição atualizada";

    @Autowired
    private CategoryRepository categoryRepository;

    private Category category;
    private CategoryRequest categoryRequest;
    
    @BeforeEach
    void setUp() {
        // Preparando objetos para os testes
        categoryRequest = CategoryRequest.builder()
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .build();

        category = Category.builder()
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CategoryResponse.builder()
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .build();
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Testes de Criação de Categoria")
    class CreateCategoryTests {

        @Test
        @DisplayName("Deve criar uma categoria com sucesso")
        void createCategory_WithValidData_ReturnsCreated() {
            // Act
            ResponseEntity<CategoryResponse> response = restTemplate.postForEntity(
                    BASE_URI,
                    categoryRequest,
                    CategoryResponse.class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).satisfies(created -> {
                assertThat(created.getId()).isNotNull();
                assertThat(created.getName()).isEqualTo(CATEGORY_NAME);
                assertThat(created.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                assertThat(created.getCreatedAt()).isNotNull();
                assertThat(created.getUpdatedAt()).isNotNull();
            });
        }

        @Test
        @DisplayName("Deve retornar erro ao criar categoria com nome em branco")
        void createCategory_WithBlankName_ReturnsBadRequest() {
            // Arrange
            categoryRequest.setName("");

            // Act
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URI,
                    categoryRequest,
                    String.class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Categoria")
    class FindCategoryTests {

        @Test
        @DisplayName("Deve retornar todas as categorias")
        void findAllCategories_ReturnsAllCategories() {
            // Arrange
            category = categoryRepository.save(category);

            // Act
            ResponseEntity<CategoryResponse[]> response = restTemplate.getForEntity(
                    BASE_URI,
                    CategoryResponse[].class
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
            assertThat(response.getBody()[0]).satisfies(found -> {
                assertThat(found.getName()).isEqualTo(CATEGORY_NAME);
                assertThat(found.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
            });
        }

        @Test
        @DisplayName("Deve retornar uma categoria por ID")
        void findCategoryById_WithValidId_ReturnsCategory() {
            // Arrange
            category = categoryRepository.save(category);

            // Act
            ResponseEntity<CategoryResponse> response = restTemplate.getForEntity(
                    BASE_URI + "/{id}",
                    CategoryResponse.class,
                    category.getId()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).satisfies(found -> {
                assertThat(found.getId()).isEqualTo(category.getId());
                assertThat(found.getName()).isEqualTo(CATEGORY_NAME);
                assertThat(found.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
            });
        }

        @Test
        @DisplayName("Deve retornar erro ao buscar categoria inexistente")
        void findCategoryById_WithInvalidId_ReturnsNotFound() {
            // Act
            ResponseEntity<String> response = restTemplate.getForEntity(
                    BASE_URI + "/{id}",
                    String.class,
                    999L
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Categoria")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Deve atualizar uma categoria com sucesso")
        void updateCategory_WithValidData_ReturnsUpdatedCategory() {
            // Arrange
            category = categoryRepository.save(category);
            CategoryRequest updateRequest = CategoryRequest.builder()
                    .name(UPDATED_NAME)
                    .description(UPDATED_DESCRIPTION)
                    .build();

            // Act
            ResponseEntity<CategoryResponse> response = restTemplate.exchange(
                    BASE_URI + "/{id}",
                    HttpMethod.PUT,
                    new HttpEntity<>(updateRequest),
                    CategoryResponse.class,
                    category.getId()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).satisfies(updated -> {
                assertThat(updated.getId()).isEqualTo(category.getId());
                assertThat(updated.getName()).isEqualTo(UPDATED_NAME);
                assertThat(updated.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
        }
    }

    @Nested
    @DisplayName("Testes de Deleção de Categoria")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Deve deletar uma categoria com sucesso")
        void deleteCategory_WithValidId_ReturnsNoContent() {
            // Arrange
            category = categoryRepository.save(category);

            // Act
            ResponseEntity<Void> response = restTemplate.exchange(
                    BASE_URI + "/{id}",
                    HttpMethod.DELETE,
                    null,
                    Void.class,
                    category.getId()
            );

            // Assert
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            assertThat(categoryRepository.findById(category.getId())).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar erro ao tentar deletar categoria inexistente")
        void deleteCategory_WithInvalidId_ReturnsNotFound() {
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