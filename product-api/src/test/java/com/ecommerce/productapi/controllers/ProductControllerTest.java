package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.exception.ProductNotFoundException;
import com.ecommerce.productapi.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@DisplayName("Testes do ProductController")
class ProductControllerTest {

        // Product mock constants
        private static final Long PRODUCT_ID = 1L;
        private static final String PRODUCT_NAME = "Smartphone";
        private static final String PRODUCT_DESCRIPTION = "Um smartphone muito legal";
        private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(1999.99);
        private static final Integer PRODUCT_QUANTITY = 10;
        private static final String PRODUCT_IDENTIFIER = "PROD-1234";
        private static final Long CATEGORY_ID = 1L;
        private static final String CATEGORY_NAME = "Eletrônicos";

        // Pagination constants
        private static final int PAGE_NUMBER = 0;
        private static final int LINES_PER_PAGE = 12;
        private static final String SORT_DIRECTION = "ASC";
        private static final String SORT_BY = "name";

        private final MockMvc mockMvc;
        private final ObjectMapper objectMapper;

        @Autowired
        public ProductControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.objectMapper = objectMapper;
        }

        @MockBean
        private ProductService productService;

        @Mock
        private PagedResourcesAssembler<ProductResponse> assembler;

        private ProductResponse createMockProductResponse() {
                return ProductResponse.builder()
                                .id(PRODUCT_ID)
                                .name(PRODUCT_NAME)
                                .description(PRODUCT_DESCRIPTION)
                                .price(PRODUCT_PRICE)
                                .quantity(PRODUCT_QUANTITY)
                                .productIdentifier(PRODUCT_IDENTIFIER)
                                .categoryId(CATEGORY_ID)
                                .categoryName(CATEGORY_NAME)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        private ProductRequest createValidProductRequest() {
                return ProductRequest.builder()
                                .name(PRODUCT_NAME)
                                .description(PRODUCT_DESCRIPTION)
                                .price(PRODUCT_PRICE)
                                .quantity(PRODUCT_QUANTITY)
                                .categoryId(CATEGORY_ID)
                                .build();
        }

        @Nested
        @DisplayName("Testes de busca de produtos")
        class FindProductsTests {

                @Test
                @DisplayName("findAllProducts - Deve retornar lista de produtos com HATEOAS")
                void whenFindAllProducts_thenReturnProductsList() throws Exception {
                        List<ProductResponse> products = List.of(createMockProductResponse());
                        when(productService.findAllProducts()).thenReturn(products);

                        mockMvc.perform(get("/products")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].id", is(PRODUCT_ID.intValue())))
                                        .andExpect(jsonPath("$[0].name", is(PRODUCT_NAME)))
                                        .andExpect(jsonPath("$[0].productIdentifier", is(PRODUCT_IDENTIFIER)))
                                        .andExpect(jsonPath("$[0].createdAt", notNullValue()))
                                        .andExpect(jsonPath("$[0].updatedAt", notNullValue()))
                                        .andExpect(jsonPath("$[0].links[0].href").exists())
                                        .andExpect(jsonPath("$[0].links[0].rel").exists());
                }

                @Test
                @DisplayName("findAllProducts - Deve retornar lista vazia")
                void whenFindAllProducts_andNoProducts_thenReturnEmptyList() throws Exception {
                        when(productService.findAllProducts()).thenReturn(Collections.emptyList());

                        mockMvc.perform(get("/products")
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(0)));
                }

                @Test
                @DisplayName("findAllPageProducts - Deve retornar página de produtos com HATEOAS")
                void whenFindAllPageProducts_thenReturnProductsPage() throws Exception {
                        ProductResponse product = createMockProductResponse();
                        Page<ProductResponse> page = new PageImpl<>(List.of(product));
                        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, LINES_PER_PAGE, Sort.Direction.ASC, SORT_BY);

                        when(productService.findAllPageProducts(any(PageRequest.class))).thenReturn(page);

                        PagedModel<EntityModel<ProductResponse>> pagedModel = PagedModel.of(
                                        List.of(EntityModel.of(product)),
                                        new PagedModel.PageMetadata(1, 0, 1));
                        when(assembler.toModel(eq(page), any(org.springframework.hateoas.Link.class))).thenReturn(pagedModel);

                        mockMvc.perform(get("/products/pageable")
                                        .param("page", String.valueOf(PAGE_NUMBER))
                                        .param("linesPerPage", String.valueOf(LINES_PER_PAGE))
                                        .param("direction", SORT_DIRECTION)
                                        .param("orderBy", SORT_BY)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$._embedded.productResponseList", hasSize(1)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].id", is(PRODUCT_ID.intValue())))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].name", is(PRODUCT_NAME)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].description", is(PRODUCT_DESCRIPTION)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].price", is(PRODUCT_PRICE.doubleValue())))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].quantity", is(PRODUCT_QUANTITY)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].productIdentifier", is(PRODUCT_IDENTIFIER)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].categoryId", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].categoryName", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].createdAt", notNullValue()))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0].updatedAt", notNullValue()))
                                        .andExpect(jsonPath("$._embedded.productResponseList[0]._links.all-products.href").exists())
                                        .andExpect(jsonPath("$._embedded.productResponseList[0]._links.self.href").exists())
                                        .andExpect(jsonPath("$._links.self.href").exists())
                                        .andExpect(jsonPath("$.page.size", is(1)))
                                        .andExpect(jsonPath("$.page.totalElements", is(1)))
                                        .andExpect(jsonPath("$.page.totalPages", is(1)))
                                        .andExpect(jsonPath("$.page.number", is(0)));

                        verify(productService).findAllPageProducts(eq(pageRequest));
                }

                @Test
                @DisplayName("findProductByCategory - Deve retornar lista de produtos por categoria")
                void whenFindProductByCategory_thenReturnProductsList() throws Exception {
                        List<ProductResponse> products = List.of(createMockProductResponse());
                        when(productService.findProductByCategoryId(CATEGORY_ID)).thenReturn(products);

                        mockMvc.perform(get("/products/category/" + CATEGORY_ID.intValue())
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].id", is(PRODUCT_ID.intValue())))
                                        .andExpect(jsonPath("$[0].name", is(PRODUCT_NAME)))
                                        .andExpect(jsonPath("$[0].description", is(PRODUCT_DESCRIPTION)))
                                        .andExpect(jsonPath("$[0].price", is(PRODUCT_PRICE.doubleValue())))
                                        .andExpect(jsonPath("$[0].quantity", is(PRODUCT_QUANTITY)))
                                        .andExpect(jsonPath("$[0].productIdentifier", is(PRODUCT_IDENTIFIER)))
                                        .andExpect(jsonPath("$[0].categoryId", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$[0].createdAt", notNullValue()))
                                        .andExpect(jsonPath("$[0].updatedAt", notNullValue()))
                                        .andExpect(jsonPath("$[0].categoryName", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$[0].links[0].href").exists())
                                        .andExpect(jsonPath("$[0].links[0].rel").exists());
                }

                @Test
                @DisplayName("findProductByCategory - Deve retornar lista vazia quando não houver produtos")
                void whenFindProductByCategory_andNoProducts_thenReturnEmptyList() throws Exception {
                        when(productService.findProductByCategoryId(CATEGORY_ID)).thenReturn(Collections.emptyList());

                        mockMvc.perform(get("/products/category/" + CATEGORY_ID.intValue())
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$", hasSize(0)));
                }

                @Test
                @DisplayName("findProductByIdentifier - Deve retornar produto por identifier")
                void whenFindProductByIdentifier_thenReturnProduct() throws Exception {
                        ProductResponse product = createMockProductResponse();
                        when(productService.findByProductIdentifier(PRODUCT_IDENTIFIER)).thenReturn(product);

                        mockMvc.perform(get("/products/" + PRODUCT_IDENTIFIER)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id", is(PRODUCT_ID.intValue())))
                                        .andExpect(jsonPath("$.name", is(PRODUCT_NAME)))
                                        .andExpect(jsonPath("$.description", is(PRODUCT_DESCRIPTION)))
                                        .andExpect(jsonPath("$.price", is(PRODUCT_PRICE.doubleValue())))
                                        .andExpect(jsonPath("$.quantity", is(PRODUCT_QUANTITY)))
                                        .andExpect(jsonPath("$.productIdentifier", is(PRODUCT_IDENTIFIER)))
                                        .andExpect(jsonPath("$.categoryId", is(CATEGORY_ID.intValue())))
                                        .andExpect(jsonPath("$.createdAt", notNullValue()))
                                        .andExpect(jsonPath("$.updatedAt", notNullValue()))
                                        .andExpect(jsonPath("$.categoryName", is(CATEGORY_NAME)))
                                        .andExpect(jsonPath("$._links.all-products.href").exists())
                                        .andExpect(jsonPath("$._links.self.href").exists());
                }

                @Test
                @DisplayName("findProductByIdentifier - Deve retornar 404 quando produto não encontrado")
                void whenFindProductByIdentifier_andProductNotFound_thenReturn404() throws Exception {
                        when(productService.findByProductIdentifier(PRODUCT_IDENTIFIER))
                                        .thenThrow(new ProductNotFoundException("Produto não encontrado"));

                        mockMvc.perform(get("/products/" + PRODUCT_IDENTIFIER)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        @DisplayName("Testes de operações de gerenciamento")
        class ManagementOperationsTests {

                @Test
                @DisplayName("newProduct - Deve criar novo produto")
                void whenCreateNewProduct_thenReturnCreated() throws Exception {
                        ProductRequest request = createValidProductRequest();
                        ProductResponse response = createMockProductResponse();
                        when(productService.save(any(ProductRequest.class))).thenReturn(response);

                        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id", is(PRODUCT_ID.intValue())))
                        .andExpect(jsonPath("$.name", is(PRODUCT_NAME)))
                        .andExpect(jsonPath("$.description", is(PRODUCT_DESCRIPTION)))
                        .andExpect(jsonPath("$.price", is(PRODUCT_PRICE.doubleValue())))
                        .andExpect(jsonPath("$.quantity", is(PRODUCT_QUANTITY)))
                        .andExpect(jsonPath("$.productIdentifier", is(PRODUCT_IDENTIFIER)))
                        .andExpect(jsonPath("$.categoryId", is(CATEGORY_ID.intValue())))
                        .andExpect(jsonPath("$.categoryName", is(CATEGORY_NAME)))
                        .andExpect(jsonPath("$.createdAt", notNullValue()))
                        .andExpect(jsonPath("$.updatedAt", notNullValue()))
                        .andExpect(jsonPath("$._links.all-products.href").exists())
                        .andExpect(jsonPath("$._links.self.href").exists());
                }

                @Test
                @DisplayName("newProduct - Deve retornar 400 quando request inválido")
                void whenCreateNewProduct_withInvalidRequest_thenReturn400() throws Exception {
                        ProductRequest invalidRequest = ProductRequest.builder().build();

                        mockMvc.perform(post("/products")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(invalidRequest)))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                @DisplayName("updateProduct - Deve atualizar produto")
                void whenUpdateProduct_thenReturnUpdatedProduct() throws Exception {
                        ProductRequest request = createValidProductRequest();
                        ProductResponse response = createMockProductResponse();
                        when(productService.update(eq(PRODUCT_IDENTIFIER), any(ProductRequest.class))).thenReturn(response);

                        mockMvc.perform(put("/products/" + PRODUCT_IDENTIFIER)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.id", is(PRODUCT_ID.intValue())))
                                        .andExpect(jsonPath("$.name", is(PRODUCT_NAME)))
                                        .andExpect(jsonPath("$._links.all-products.href").exists())
                                        .andExpect(jsonPath("$._links.self.href").exists());
                }

                @Test
                @DisplayName("updateProduct - Deve retornar 404 quando produto não encontrado")
                void whenUpdateProduct_andProductNotFound_thenReturn404() throws Exception {
                        ProductRequest request = createValidProductRequest();
                        when(productService.update(eq(PRODUCT_IDENTIFIER), any(ProductRequest.class)))
                                        .thenThrow(new ProductNotFoundException("Produto não encontrado"));

                        mockMvc.perform(put("/products/" + PRODUCT_IDENTIFIER)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isNotFound());
                }

                @Test
                @DisplayName("delete - Deve deletar produto")
                void whenDeleteProduct_thenReturnNoContent() throws Exception {
                        doNothing().when(productService).delete(PRODUCT_ID);

                        mockMvc.perform(delete("/products/" + PRODUCT_ID)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isNoContent());

                        verify(productService, times(1)).delete(PRODUCT_ID);
                }

                @Test
                @DisplayName("delete - Deve retornar 404 quando produto não encontrado")
                void whenDeleteProduct_andProductNotFound_thenReturn404() throws Exception {
                        doThrow(new ProductNotFoundException("Produto não encontrado"))
                                        .when(productService).delete(PRODUCT_ID);

                        mockMvc.perform(delete("/products/" + PRODUCT_ID)
                                        .accept(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isNotFound());
                }
        }
}