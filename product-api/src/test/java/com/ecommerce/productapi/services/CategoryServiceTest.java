package com.ecommerce.productapi.services;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.mappers.impl.CategoryMapper;
import com.ecommerce.productapi.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Testes do CategoryService")
class CategoryServiceTest {

    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;
    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String CATEGORY_DESCRIPTION = "Produtos eletrônicos em geral";
    private static final String ERROR_MESSAGE_TEMPLATE = "Categoria não encontrado com id: '%d'";

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper mapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        category = createCategory();
        categoryRequest = createCategoryRequest();
        categoryResponse = createCategoryResponse();
    }

    @Nested
    @DisplayName("Testes de busca de categorias")
    class FindCategoriesTests {
        
        @Test
        @DisplayName("Deve retornar lista de categorias quando existirem registros")
        void shouldReturnCategoryList_WhenCategoriesExist() {
            // Arrange
            List<Category> categories = List.of(category);
            when(categoryRepository.findAll()).thenReturn(categories);
            when(mapper.toResponse(any(Category.class))).thenReturn(categoryResponse);

            // Act
            List<CategoryResponse> result = categoryService.findAllCategories();

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .first()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(CATEGORY_NAME);
                        assertThat(response.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                    });

            verify(categoryRepository).findAll();
            verify(mapper, times(1)).toResponse(any(Category.class));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem categorias")
        void shouldReturnEmptyList_WhenNoCategoriesExist() {
            // Arrange
            when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

            // Act
            List<CategoryResponse> result = categoryService.findAllCategories();

            // Assert
            assertThat(result).isEmpty();
            verify(categoryRepository).findAll();
            verify(mapper, never()).toResponse(any(Category.class));
        }

        @Test
        @DisplayName("Deve retornar categoria por ID quando existir")
        void shouldReturnCategory_WhenIdExists() {
            // Arrange
            when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));
            when(mapper.toResponse(category)).thenReturn(categoryResponse);

            // Act
            CategoryResponse result = categoryService.findCategoryById(VALID_ID);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(CATEGORY_NAME);
                        assertThat(response.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                    });

            verify(categoryRepository).findById(VALID_ID);
            verify(mapper).toResponse(category);
        }

        @Test
        @DisplayName("Deve lançar exceção quando buscar por ID inexistente")
        void shouldThrowException_WhenIdDoesNotExist() {
            // Arrange
            when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.findCategoryById(INVALID_ID))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(String.format(ERROR_MESSAGE_TEMPLATE, INVALID_ID));

            verify(categoryRepository).findById(INVALID_ID);
            verify(mapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("Testes de operações de persistência")
    class PersistenceOperationsTests {

        @Test
        @DisplayName("Deve salvar categoria com sucesso")
        void shouldSaveCategory_Successfully() {
            // Arrange
            when(mapper.toEntity(categoryRequest)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);
            when(mapper.toResponse(category)).thenReturn(categoryResponse);

            // Act
            CategoryResponse result = categoryService.save(categoryRequest);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(CATEGORY_NAME);
                        assertThat(response.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                    });

            verify(mapper).toEntity(categoryRequest);
            verify(categoryRepository).save(category);
            verify(mapper).toResponse(category);
        }

        @Test
        @DisplayName("Deve atualizar categoria existente com sucesso")
        void shouldUpdateCategory_WhenExists() {
            // Arrange
            when(categoryRepository.findById(VALID_ID)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);
            when(mapper.toResponse(category)).thenReturn(categoryResponse);

            // Act
            CategoryResponse result = categoryService.update(VALID_ID, categoryRequest);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .satisfies(response -> {
                        assertThat(response.getId()).isEqualTo(VALID_ID);
                        assertThat(response.getName()).isEqualTo(CATEGORY_NAME);
                        assertThat(response.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                    });

            verify(categoryRepository).findById(VALID_ID);
            verify(categoryRepository).save(category);
            verify(mapper).toResponse(category);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar categoria inexistente")
        void shouldThrowException_WhenUpdatingNonExistentCategory() {
            // Arrange
            when(categoryRepository.findById(INVALID_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> categoryService.update(INVALID_ID, categoryRequest))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(String.format(ERROR_MESSAGE_TEMPLATE, INVALID_ID));

            verify(categoryRepository).findById(INVALID_ID);
            verify(categoryRepository, never()).save(any());
            verify(mapper, never()).toResponse(any());
        }

        @Test
        @DisplayName("Deve deletar categoria existente com sucesso")
        void shouldDeleteCategory_WhenExists() {
            // Arrange
            when(categoryRepository.existsById(VALID_ID)).thenReturn(true);

            // Act
            categoryService.delete(VALID_ID);

            // Assert
            verify(categoryRepository).existsById(VALID_ID);
            verify(categoryRepository).deleteById(VALID_ID);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar deletar categoria inexistente")
        void shouldThrowException_WhenDeletingNonExistentCategory() {
            // Arrange
            when(categoryRepository.existsById(INVALID_ID)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> categoryService.delete(INVALID_ID))
                    .isInstanceOf(CategoryNotFoundException.class)
                    .hasMessage(String.format(ERROR_MESSAGE_TEMPLATE, INVALID_ID));

            verify(categoryRepository).existsById(INVALID_ID);
            verify(categoryRepository, never()).deleteById(any());
        }
    }

    private Category createCategory() {
        return Category.builder()
                .id(VALID_ID)
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .build();
    }

    private CategoryRequest createCategoryRequest() {
        CategoryRequest request = new CategoryRequest();
        request.setName(CATEGORY_NAME);
        request.setDescription(CATEGORY_DESCRIPTION);
        return request;
    }

    private CategoryResponse createCategoryResponse() {
        CategoryResponse response = new CategoryResponse();
        response.setId(VALID_ID);
        response.setName(CATEGORY_NAME);
        response.setDescription(CATEGORY_DESCRIPTION);
        return response;
    }
}