package com.ecommerce.productapi.repositories;

import com.ecommerce.productapi.domain.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do CategoryRepository")
class CategoryRepositoryTest {

    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String CATEGORY_DESCRIPTION = "Categoria de produtos eletrônicos";
    private static final Long INVALID_CATEGORY_ID = 999L;

    private final CategoryRepository categoryRepository;
    private final TestEntityManager entityManager;
    private Category category;

    @Autowired
    CategoryRepositoryTest(CategoryRepository categoryRepository, TestEntityManager entityManager) {
        this.categoryRepository = categoryRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        category = createAndPersistCategory(CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }

    @Nested
    @DisplayName("Testes de persistência")
    class PersistenceTests {

        @Test
        @DisplayName("Deve salvar categoria com sucesso")
        void shouldSaveCategory_Successfully() {
            // Arrange
            Category newCategory = createCategoryEntity("Livros", "Categoria de livros");

            // Act
            Category savedCategory = categoryRepository.save(newCategory);

            // Assert
            assertThat(savedCategory)
                    .isNotNull()
                    .satisfies(c -> {
                        assertThat(c.getId()).isNotNull();
                        assertThat(c.getName()).isEqualTo("Livros");
                        assertThat(c.getDescription()).isEqualTo("Categoria de livros");
                    });

            Category foundCategory = entityManager.find(Category.class, savedCategory.getId());
            assertThat(foundCategory)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(savedCategory);
        }

        @Test
        @DisplayName("Deve deletar categoria com sucesso")
        void shouldDeleteCategory_Successfully() {
            // Arrange
            Long categoryId = category.getId();

            // Act
            categoryRepository.deleteById(categoryId);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Category foundCategory = entityManager.find(Category.class, categoryId);
            assertThat(foundCategory).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de busca")
    class FindTests {

        @Test
        @DisplayName("Deve encontrar categoria por ID quando existir")
        void shouldFindCategory_WhenIdExists() {
            // Act
            Optional<Category> result = categoryRepository.findById(category.getId());

            // Assert
            assertThat(result)
                    .isPresent()
                    .get()
                    .satisfies(c -> {
                        assertThat(c.getId()).isEqualTo(category.getId());
                        assertThat(c.getName()).isEqualTo(CATEGORY_NAME);
                        assertThat(c.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                    });
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existir")
        void shouldReturnEmptyOptional_WhenIdDoesNotExist() {
            // Act
            Optional<Category> result = categoryRepository.findById(INVALID_CATEGORY_ID);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar todas as categorias")
        void shouldReturnAllCategories() {
            // Arrange
            createAndPersistCategory("Livros", "Categoria de livros"); // Segunda categoria

            // Act
            List<Category> result = categoryRepository.findAll();

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .satisfies(categories -> {
                        assertThat(categories)
                                .extracting(Category::getName)
                                .containsExactlyInAnyOrder(CATEGORY_NAME, "Livros");
                        
                        assertThat(categories)
                                .extracting(Category::getDescription)
                                .containsExactlyInAnyOrder(CATEGORY_DESCRIPTION, "Categoria de livros");
                    });
        }
    }

    @Nested
    @DisplayName("Testes de verificação de existência")
    class ExistsTests {

        @Test
        @DisplayName("Deve confirmar existência quando categoria existir")
        void shouldConfirmExistence_WhenCategoryExists() {
            // Act
            boolean exists = categoryRepository.existsById(category.getId());

            // Assert
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Deve negar existência quando categoria não existir")
        void shouldDenyExistence_WhenCategoryDoesNotExist() {
            // Act
            boolean exists = categoryRepository.existsById(INVALID_CATEGORY_ID);

            // Assert
            assertThat(exists).isFalse();
        }
    }

    private Category createAndPersistCategory(String name, String description) {
        Category newCategory = createCategoryEntity(name, description);
        entityManager.persist(newCategory);
        entityManager.flush();
        return newCategory;
    }

    private Category createCategoryEntity(String name, String description) {
        return Category.builder()
                .name(name)
                .description(description)
                .build();
    }
}