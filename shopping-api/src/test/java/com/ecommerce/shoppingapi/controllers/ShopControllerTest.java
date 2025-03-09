package com.ecommerce.shoppingapi.controllers;

import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.services.ShopService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopController.class)
@ActiveProfiles("test")
@DisplayName("Testes do ShopController")
class ShopControllerTest {

    // Shop mock constants
    private static final Long SHOP_ID = 1L;
    private static final String USER_IDENTIFIER = "123.456.789-00";
    private static final BigDecimal SHOP_TOTAL = BigDecimal.valueOf(199.99);
    
    // Item constants
    private static final String PRODUCT_IDENTIFIER_1 = "PROD-001";
    private static final String PRODUCT_IDENTIFIER_2 = "PROD-002";
    private static final BigDecimal ITEM_PRICE_1 = BigDecimal.valueOf(99.99);
    private static final BigDecimal ITEM_PRICE_2 = BigDecimal.valueOf(100.00);
    
    // Pagination constants
    private static final int PAGE_NUMBER = 0;
    private static final int LINES_PER_PAGE = 12;
    private static final String SORT_DIRECTION = "ASC";
    private static final String SORT_BY = "total";

    // Date constants for filters
    private static final LocalDate START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2023, 12, 31);
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(200.00);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShopService shopService;

    // Helper methods to create test data
    private ShopResponseDto createMockShopResponse() {
        return ShopResponseDto.builder()
                .id(SHOP_ID)
                .userIdentifier(USER_IDENTIFIER)
                .total(SHOP_TOTAL)
                .date(LocalDateTime.now())
                .items(createMockItems())
                .build();
    }

    private ShopRequestDto createValidShopRequest() {
        return ShopRequestDto.builder()
                .userIdentifier(USER_IDENTIFIER)
                .items(createMockItems())
                .build();
    }

    private List<ItemDto> createMockItems() {
        return Arrays.asList(
                ItemDto.builder()
                        .productIdentifier(PRODUCT_IDENTIFIER_1)
                        .price(ITEM_PRICE_1)
                        .build(),
                ItemDto.builder()
                        .productIdentifier(PRODUCT_IDENTIFIER_2)
                        .price(ITEM_PRICE_2)
                        .build()
        );
    }

    @Nested
    @DisplayName("Testes de busca de compras")
    class FindShopTests {

        @Test
        @DisplayName("getAllShops - Deve retornar lista de compras")
        void whenGetAllShops_thenReturnShopList() throws Exception {
            // Arrange
            List<ShopResponseDto> shops = Arrays.asList(createMockShopResponse());
            when(shopService.getAll()).thenReturn(shops);

            // Act & Assert
            mockMvc.perform(get("/shoppings")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$[0].userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$[0].total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$[0].date", notNullValue()))
                    .andExpect(jsonPath("$[0].items", hasSize(2)))
                    .andExpect(jsonPath("$[0].items[0].productIdentifier", is(PRODUCT_IDENTIFIER_1)))
                    .andExpect(jsonPath("$[0].items[0].price", is(ITEM_PRICE_1.doubleValue())))
                    .andExpect(jsonPath("$[0].items[1].productIdentifier", is(PRODUCT_IDENTIFIER_2)))
                    .andExpect(jsonPath("$[0].items[1].price", is(ITEM_PRICE_2.doubleValue())));

            // Verify
            verify(shopService, times(1)).getAll();
        }

        @Test
        @DisplayName("getAllShops - Deve retornar lista vazia quando não houver compras")
        void whenGetAllShops_andNoShops_thenReturnEmptyList() throws Exception {
            // Arrange
            when(shopService.getAll()).thenReturn(Collections.emptyList());

            // Act & Assert
            mockMvc.perform(get("/shoppings")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            // Verify
            verify(shopService, times(1)).getAll();
        }

        @Test
        @DisplayName("getAllShopsPage - Deve retornar página de compras")
        void whenGetAllShopsPage_thenReturnShopsPage() throws Exception {
            // Arrange
            ShopResponseDto shop = createMockShopResponse();
            List<ShopResponseDto> shops = Arrays.asList(shop);
            Page<ShopResponseDto> page = new PageImpl<>(shops);
            
            // Configurar o mock para retornar a página de compras
            when(shopService.getAllPage(any(PageRequest.class))).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get("/shoppings/pageable")
                    .param("page", String.valueOf(PAGE_NUMBER))
                    .param("linesPerPage", String.valueOf(LINES_PER_PAGE))
                    .param("direction", SORT_DIRECTION)
                    .param("orderBy", SORT_BY)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$.content[0].userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$.content[0].total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$.content[0].date", notNullValue()))
                    .andExpect(jsonPath("$.content[0].items", hasSize(2)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.size", is(1)))
                    .andExpect(jsonPath("$.number", is(0)));

            // Verify
            verify(shopService, times(1)).getAllPage(any(PageRequest.class));
        }

        @Test
        @DisplayName("getShopsByUserIdentifier - Deve retornar compras de um usuário específico")
        void whenGetShopsByUserIdentifier_thenReturnUserShops() throws Exception {
            // Arrange
            List<ShopResponseDto> shops = Arrays.asList(createMockShopResponse());
            when(shopService.getByUser(USER_IDENTIFIER)).thenReturn(shops);

            // Act & Assert
            mockMvc.perform(get("/shoppings/shopByUser/" + USER_IDENTIFIER)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$[0].userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$[0].total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$[0].date", notNullValue()))
                    .andExpect(jsonPath("$[0].items", hasSize(2)));

            // Verify
            verify(shopService, times(1)).getByUser(USER_IDENTIFIER);
        }

        @Test
        @DisplayName("findById - Deve retornar compra por ID")
        void whenFindById_thenReturnShop() throws Exception {
            // Arrange
            ShopResponseDto shop = createMockShopResponse();
            when(shopService.findById(SHOP_ID)).thenReturn(shop);

            // Act & Assert
            mockMvc.perform(get("/shoppings/" + SHOP_ID)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$.userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$.total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$.date", notNullValue()))
                    .andExpect(jsonPath("$.items", hasSize(2)));

            // Verify
            verify(shopService, times(1)).findById(SHOP_ID);
        }

        @Test
        @DisplayName("findById - Deve retornar 404 quando compra não encontrada")
        void whenFindById_andShopNotFound_thenReturn404() throws Exception {
            // Arrange
            when(shopService.findById(SHOP_ID)).thenThrow(new ShoppingNotFoundException("id", SHOP_ID));

            // Act & Assert
            mockMvc.perform(get("/shoppings/" + SHOP_ID)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            // Verify
            verify(shopService, times(1)).findById(SHOP_ID);
        }

        @Test
        @DisplayName("getShopsByFilter - Deve filtrar compras por data e valor")
        void whenGetShopsByFilter_thenReturnFilteredShops() throws Exception {
            // Arrange
            List<ShopResponseDto> shops = Arrays.asList(createMockShopResponse());
            when(shopService.getShopsByFilter(eq(START_DATE), eq(END_DATE), eq(MAX_VALUE))).thenReturn(shops);

            // Act & Assert
            mockMvc.perform(get("/shoppings/search")
                    .param("startDate", "01/01/2023")
                    .param("endDate", "31/12/2023")
                    .param("maxValue", MAX_VALUE.toString())
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$[0].userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$[0].total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$[0].items", hasSize(2)));

            // Verify
            verify(shopService, times(1)).getShopsByFilter(eq(START_DATE), eq(END_DATE), eq(MAX_VALUE));
        }

        @Test
        @DisplayName("getShopsByFilter - Deve filtrar compras somente com data inicial obrigatória")
        void whenGetShopsByFilter_withOnlyStartDate_thenReturnFilteredShops() throws Exception {
            // Arrange
            List<ShopResponseDto> shops = Arrays.asList(createMockShopResponse());
            when(shopService.getShopsByFilter(eq(START_DATE), eq(null), eq(null))).thenReturn(shops);

            // Act & Assert
            mockMvc.perform(get("/shoppings/search")
                    .param("startDate", "01/01/2023")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)));

            // Verify
            verify(shopService, times(1)).getShopsByFilter(eq(START_DATE), eq(null), eq(null));
        }

        @Test
        @DisplayName("getReportByDate - Deve retornar relatório de compras por período")
        void whenGetReportByDate_thenReturnShoppingReport() throws Exception {
            // Arrange
            ShopReportResponseDto report = ShopReportResponseDto.builder()
                    .count(2)
                    .mean(BigDecimal.valueOf(100.00))
                    .total(BigDecimal.valueOf(200.00))
                    .build();
            
            when(shopService.getReportByDate(START_DATE, END_DATE)).thenReturn(report);

            // Act & Assert
            mockMvc.perform(get("/shoppings/report")
                    .param("startDate", "01/01/2023")
                    .param("endDate", "31/12/2023")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.count", is(2)))
                    .andExpect(jsonPath("$.mean", is(100)))
                    .andExpect(jsonPath("$.total", is(200.00)));

            // Verify
            verify(shopService, times(1)).getReportByDate(START_DATE, END_DATE);
        }

        @Test
        @DisplayName("getReportByDate - Deve formatar valores e retornar zeros quando não há compras")
        void whenGetReportByDate_andNoShops_thenReturnZeros() throws Exception {
            // Arrange
            ShopReportResponseDto report = ShopReportResponseDto.builder()
                    .count(0)
                    .mean(null)
                    .total(null)
                    .build();
            
            when(shopService.getReportByDate(START_DATE, END_DATE)).thenReturn(report);

            // Act & Assert
            mockMvc.perform(get("/shoppings/report")
                    .param("startDate", "01/01/2023")
                    .param("endDate", "31/12/2023")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.count", is(0)))
                    .andExpect(jsonPath("$.mean", is(0)))
                    .andExpect(jsonPath("$.total", is(0)));

            // Verify
            verify(shopService, times(1)).getReportByDate(START_DATE, END_DATE);
        }
    }

    @Nested
    @DisplayName("Testes de operações de gerenciamento")
    class ManagementOperationsTests {

        @Test
        @DisplayName("newShop - Deve criar nova compra")
        void whenNewShop_thenReturnCreated() throws Exception {
            // Arrange
            ShopRequestDto request = createValidShopRequest();
            ShopResponseDto response = createMockShopResponse();
            when(shopService.save(any(ShopRequestDto.class))).thenReturn(response);

            // Act & Assert
            mockMvc.perform(post("/shoppings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(SHOP_ID.intValue())))
                    .andExpect(jsonPath("$.userIdentifier", is(USER_IDENTIFIER)))
                    .andExpect(jsonPath("$.total", is(SHOP_TOTAL.doubleValue())))
                    .andExpect(jsonPath("$.date", notNullValue()))
                    .andExpect(jsonPath("$.items", hasSize(2)));

            // Verify
            verify(shopService, times(1)).save(any(ShopRequestDto.class));
        }

        @Test
        @DisplayName("newShop - Deve retornar 400 quando request inválido")
        void whenNewShop_withInvalidRequest_thenReturn400() throws Exception {
            // Arrange - request vazio
            ShopRequestDto invalidRequest = new ShopRequestDto();

            // Act & Assert
            mockMvc.perform(post("/shoppings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());

            // Verify
            verify(shopService, never()).save(any(ShopRequestDto.class));
        }

        @Test
        @DisplayName("deleteShop - Deve deletar compra")
        void whenDeleteShop_thenReturnNoContent() throws Exception {
            // Arrange
            doNothing().when(shopService).delete(SHOP_ID);

            // Act & Assert
            mockMvc.perform(delete("/shoppings/" + SHOP_ID)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            // Verify
            verify(shopService, times(1)).delete(SHOP_ID);
        }

        @Test
        @DisplayName("deleteShop - Deve retornar 404 quando compra não encontrada")
        void whenDeleteShop_andShopNotFound_thenReturn404() throws Exception {
            // Arrange
            doThrow(new ShoppingNotFoundException("id", SHOP_ID)).when(shopService).delete(SHOP_ID);

            // Act & Assert
            mockMvc.perform(delete("/shoppings/" + SHOP_ID)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            // Verify
            verify(shopService, times(1)).delete(SHOP_ID);
        }
    }
}