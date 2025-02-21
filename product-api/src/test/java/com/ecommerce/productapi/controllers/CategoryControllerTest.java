package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@ActiveProfiles("test")
@DisplayName("Testes do CategoryController")
class CategoryControllerTest {

        // Category mock constants
        private static final Long CATEGORY_ID = 1L;
        private static final String CATEGORY_NAME = "Eletrônicos";
        private static final String CATEGORY_DESCRIPTION = "Produtos eletrônicos em geral";

        private final MockMvc mockMvc;
        private final ObjectMapper objectMapper;

        @Autowired
        public CategoryControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.objectMapper = objectMapper;
        }

        @MockBean
        private CategoryService categoryService;

        private CategoryResponse createMockCategoryResponse() {
                return CategoryResponse.builder()
                                .id(CATEGORY_ID)
                                .name(CATEGORY_NAME)
                                .description(CATEGORY_DESCRIPTION)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        private CategoryRequest createValidCategoryRequest() {
                return CategoryRequest.builder()
                                .name(CATEGORY_NAME)
                                .description(CATEGORY_DESCRIPTION)
                                .build();
        }

        @Nested
        @DisplayName("Testes de busca de categorias")
        class FindCategoriesTests {

                @Test
                @DisplayName("findAllCategories - Deve retornar lista de categorias com sucesso")
                void whenFindAllCategories_thenReturnCategoriesList() throws Exception {
                        // Arrange
                        List<CategoryResponse> categories = List.of(createMockCategoryResponse());
                        when(categoryService.findAllCategories()).thenReturn(categories);

                        // Act & Assert
                        mockMvc.perform(get("/categories"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].id", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$[0].name", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$[0].description", is(CATEGORY_DESCRIPTION)));
                }

                @Test
                @DisplayName("findAllCategories - Deve retornar lista vazia quando não houver categorias")
                void whenFindAllCategories_andNoCategories_thenReturnEmptyList() throws Exception {
                        // Arrange
                        when(categoryService.findAllCategories()).thenReturn(Collections.emptyList());

                        // Act & Assert
                        mockMvc.perform(get("/categories"))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(0)));
                }

                @Test
                @DisplayName("findCategoryById - Deve retornar categoria por ID com sucesso")
                void whenFindCategoryById_thenReturnCategory() throws Exception {
                        // Arrange
                        CategoryResponse category = createMockCategoryResponse();
                        when(categoryService.findCategoryById(CATEGORY_ID)).thenReturn(category);

                        // Act & Assert
                        mockMvc.perform(get("/categories/" + CATEGORY_ID))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$.name", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$.description", is(CATEGORY_DESCRIPTION)));
                }

                @Test
                @DisplayName("findCategoryById - Deve retornar 404 quando categoria não for encontrada")
                void whenFindCategoryById_andCategoryNotFound_thenReturn404() throws Exception {
                        // Arrange
                        when(categoryService.findCategoryById(CATEGORY_ID))
                                        .thenThrow(new CategoryNotFoundException("id", CATEGORY_ID));

                        // Act & Assert
                        mockMvc.perform(get("/categories/" + CATEGORY_ID))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        @DisplayName("Testes de operações de gerenciamento")
        class ManagementOperationsTests {

                @Test
                @DisplayName("newCategory - Deve criar nova categoria com sucesso")
                void whenCreateNewCategory_thenReturnCreated() throws Exception {
                        // Arrange
                        CategoryRequest request = createValidCategoryRequest();
                        CategoryResponse response = createMockCategoryResponse();
                        when(categoryService.save(any(CategoryRequest.class))).thenReturn(response);

                        // Act & Assert
                        mockMvc.perform(post("/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isCreated())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$.name", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$.description", is(CATEGORY_DESCRIPTION)));
                }

                @Test
                @DisplayName("newCategory - Deve retornar 400 quando request for inválido")
                void whenCreateNewCategory_withInvalidRequest_thenReturn400() throws Exception {
                        // Arrange
                        CategoryRequest invalidRequest = CategoryRequest.builder().build();

                        // Act & Assert
                        mockMvc.perform(post("/categories")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("updateCategory - Deve atualizar categoria com sucesso")
                void whenUpdateCategory_thenReturnUpdatedCategory() throws Exception {
                        // Arrange
                        CategoryRequest request = createValidCategoryRequest();
                        CategoryResponse response = createMockCategoryResponse();
                        when(categoryService.update(eq(CATEGORY_ID), any(CategoryRequest.class))).thenReturn(response);

                        // Act & Assert
                        mockMvc.perform(put("/categories/" + CATEGORY_ID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$.name", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$.description", is(CATEGORY_DESCRIPTION)));
                }

                @Test
                @DisplayName("updateCategory - Deve retornar 404 quando categoria não for encontrada")
                void whenUpdateCategory_andCategoryNotFound_thenReturn404() throws Exception {
                        // Arrange
                        CategoryRequest request = createValidCategoryRequest();
                        when(categoryService.update(eq(CATEGORY_ID), any(CategoryRequest.class)))
                                        .thenThrow(new CategoryNotFoundException("id", CATEGORY_ID));

                        // Act & Assert
                        mockMvc.perform(put("/categories/" + CATEGORY_ID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isNotFound());
                }

                @Test
                @DisplayName("updateCategory - Deve retornar 400 quando request for inválido")
                void whenUpdateCategory_withInvalidRequest_thenReturn400() throws Exception {
                        // Arrange
                        CategoryRequest invalidRequest = CategoryRequest.builder().build();

                        // Act & Assert
                        mockMvc.perform(put("/categories/" + CATEGORY_ID)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("delete - Deve deletar categoria com sucesso")
                void whenDeleteCategory_thenReturnNoContent() throws Exception {
                        // Arrange
                        doNothing().when(categoryService).delete(CATEGORY_ID);

                        // Act & Assert
                        mockMvc.perform(delete("/categories/" + CATEGORY_ID))
                                        .andExpect(status().isNoContent());

                        verify(categoryService, times(1)).delete(CATEGORY_ID);
                }

                @Test
                @DisplayName("delete - Deve retornar 404 quando categoria não for encontrada")
                void whenDeleteCategory_andCategoryNotFound_thenReturn404() throws Exception {
                        // Arrange
                        doThrow(new CategoryNotFoundException("id", CATEGORY_ID))
                                        .when(categoryService).delete(CATEGORY_ID);

                        // Act & Assert
                        mockMvc.perform(delete("/categories/" + CATEGORY_ID))
                                        .andExpect(status().isNotFound());
                }
        }
}