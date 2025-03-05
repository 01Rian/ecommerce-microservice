package com.ecommerce.shoppingapi.mappers.impl;

import com.ecommerce.shoppingapi.domain.dto.shop.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Item;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopMapperTest {

    // Constantes para os testes
    private static final Long SHOP_ID = 1L;
    private static final String USER_IDENTIFIER = "12345678901";
    private static final BigDecimal SHOP_TOTAL = BigDecimal.valueOf(100.0);
    private static final String PRODUCT_IDENTIFIER = "PROD-001";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(50.0);

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ShopMapper shopMapper;

    // Objetos de teste
    private Shop shop;
    private ShopRequestDto shopRequestDto;
    private ShopResponseDto shopResponseDto;
    private Item item;
    private ItemDto itemDto;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();

        // Setup para Item e ItemDto
        item = new Item();
        item.setProductIdentifier(PRODUCT_IDENTIFIER);
        item.setPrice(PRODUCT_PRICE);

        itemDto = ItemDto.builder()
                .productIdentifier(PRODUCT_IDENTIFIER)
                .price(PRODUCT_PRICE)
                .build();

        // Setup para Shop
        shop = new Shop();
        shop.setId(SHOP_ID);
        shop.setUserIdentifier(USER_IDENTIFIER);
        shop.setTotal(SHOP_TOTAL);
        shop.setDate(testDate);
        shop.setItems(Arrays.asList(item));

        // Setup para ShopRequestDto
        shopRequestDto = ShopRequestDto.builder()
                .userIdentifier(USER_IDENTIFIER)
                .items(Arrays.asList(itemDto))
                .build();

        // Setup para ShopResponseDto
        shopResponseDto = ShopResponseDto.builder()
                .id(SHOP_ID)
                .userIdentifier(USER_IDENTIFIER)
                .total(SHOP_TOTAL)
                .date(testDate)
                .items(Arrays.asList(itemDto))
                .build();
    }

    @Nested
    @DisplayName("Testes de Conversão Entity para ResponseDTO")
    class ToResponseTests {

        @Test
        @DisplayName("Deve converter Shop para ShopResponseDto corretamente")
        void toResponse_ShouldConvertShopToShopResponseDto() {
            // Arrange
            when(modelMapper.map(shop, ShopResponseDto.class)).thenReturn(shopResponseDto);

            // Act
            ShopResponseDto result = shopMapper.toResponse(shop);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(SHOP_ID);
            assertThat(result.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
            assertThat(result.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
            assertThat(result.getDate()).isEqualTo(testDate);
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems().get(0).getProductIdentifier()).isEqualTo(PRODUCT_IDENTIFIER);
            assertThat(result.getItems().get(0).getPrice()).isEqualByComparingTo(PRODUCT_PRICE);
            
            verify(modelMapper).map(shop, ShopResponseDto.class);
        }
    }

    @Nested
    @DisplayName("Testes de Conversão RequestDTO para Entity")
    class FromRequestTests {

        @Test
        @DisplayName("Deve converter ShopRequestDto para Shop corretamente")
        void fromRequest_ShouldConvertShopRequestDtoToShop() {
            // Arrange
            when(modelMapper.map(shopRequestDto, Shop.class)).thenReturn(shop);

            // Act
            Shop result = shopMapper.fromRequest(shopRequestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(SHOP_ID);
            assertThat(result.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
            assertThat(result.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
            assertThat(result.getDate()).isEqualTo(testDate);
            assertThat(result.getItems()).hasSize(1);
            assertThat(result.getItems().get(0).getProductIdentifier()).isEqualTo(PRODUCT_IDENTIFIER);
            assertThat(result.getItems().get(0).getPrice()).isEqualByComparingTo(PRODUCT_PRICE);
            
            verify(modelMapper).map(shopRequestDto, Shop.class);
        }
    }
}