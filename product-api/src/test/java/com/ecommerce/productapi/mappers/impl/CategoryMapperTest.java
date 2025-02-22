package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.domain.entities.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {

    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Eletrônicos";
    private static final String CATEGORY_DESCRIPTION = "Produtos eletrônicos em geral";

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CategoryMapper categoryMapper;

    private LocalDateTime now;
    private Category category;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        category = Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .createdAt(now)
                .updatedAt(now)
                .build();

        categoryRequest = CategoryRequest.builder()
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .build();

        categoryResponse = CategoryResponse.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .description(CATEGORY_DESCRIPTION)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("Testes de conversão para Response")
    class ToResponseTests {

        @Test
        @DisplayName("Deve converter Category para CategoryResponse corretamente")
        void toResponse_WithValidCategory_ReturnsCategoryResponse() {
            // Arrange
            when(modelMapper.map(category, CategoryResponse.class)).thenReturn(categoryResponse);

            // Act
            CategoryResponse result = categoryMapper.toResponse(category);

            // Assert
            assertThat(result).satisfies(response -> {
                assertThat(response.getId()).isEqualTo(CATEGORY_ID);
                assertThat(response.getName()).isEqualTo(CATEGORY_NAME);
                assertThat(response.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
                assertThat(response.getCreatedAt()).isEqualTo(now);
                assertThat(response.getUpdatedAt()).isEqualTo(now);
            });
        }

        @Test
        @DisplayName("Deve retornar null quando Category for null")
        void toResponse_WithNullCategory_ReturnsNull() {
            // Arrange
            when(modelMapper.map(null, CategoryResponse.class)).thenReturn(null);

            // Act
            CategoryResponse result = categoryMapper.toResponse(null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de conversão para Entity")
    class ToEntityTests {

        @Test
        @DisplayName("Deve converter CategoryRequest para Category corretamente")
        void toEntity_WithValidRequest_ReturnsCategory() {
            // Arrange
            Category expectedCategory = Category.builder()
                    .name(CATEGORY_NAME)
                    .description(CATEGORY_DESCRIPTION)
                    .build();

            when(modelMapper.map(categoryRequest, Category.class)).thenReturn(expectedCategory);

            // Act
            Category result = categoryMapper.toEntity(categoryRequest);

            // Assert
            assertThat(result).satisfies(entity -> {
                assertThat(entity.getName()).isEqualTo(CATEGORY_NAME);
                assertThat(entity.getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
            });
        }

        @Test
        @DisplayName("Deve retornar null quando CategoryRequest for null")
        void toEntity_WithNullRequest_ReturnsNull() {
            // Arrange
            when(modelMapper.map(null, Category.class)).thenReturn(null);

            // Act
            Category result = categoryMapper.toEntity(null);

            // Assert
            assertThat(result).isNull();
        }
    }
}