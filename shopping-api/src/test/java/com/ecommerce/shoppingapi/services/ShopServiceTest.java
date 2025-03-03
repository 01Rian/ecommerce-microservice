package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ItemDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import com.ecommerce.shoppingapi.exception.ShoppingNotFoundException;
import com.ecommerce.shoppingapi.mappers.impl.ShopMapper;
import com.ecommerce.shoppingapi.repositories.ShopRepository;
import com.ecommerce.shoppingapi.repositories.impl.ReportRepositoryImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    // Constantes para identificação
    private static final Long SHOP_ID = 1L;
    private static final String USER_IDENTIFIER = "123.456.789-00";
    private static final String PRODUCT_IDENTIFIER = "PROD-1";
    
    // Constantes para valores
    private static final BigDecimal SHOP_TOTAL = BigDecimal.valueOf(100.0);
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(100.0);
    private static final BigDecimal MAX_VALUE = BigDecimal.valueOf(1000.0);
    
    // Constantes para paginação
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    
    // Constantes para mensagens de erro
    private static final String SHOP_NOT_FOUND_MESSAGE = "Shopping não encontrado com id: '";
    
    @Mock
    private ShopRepository shopRepository;

    @Mock
    private ReportRepositoryImpl reportRepository;

    @Mock
    private ShopMapper mapper;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShopService shopService;

    private Shop shop;
    private ShopResponseDto shopResponseDto;
    private ShopRequestDto shopRequestDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        shop = new Shop();
        shop.setId(SHOP_ID);
        shop.setUserIdentifier(USER_IDENTIFIER);
        shop.setTotal(SHOP_TOTAL);
        shop.setDate(LocalDateTime.now());

        shopResponseDto = ShopResponseDto.builder()
            .id(SHOP_ID)
            .userIdentifier(USER_IDENTIFIER)
            .total(SHOP_TOTAL)
            .build();

        itemDto = ItemDto.builder()
            .productIdentifier(PRODUCT_IDENTIFIER)
            .price(PRODUCT_PRICE)
            .build();

        shopRequestDto = ShopRequestDto.builder()
            .userIdentifier(USER_IDENTIFIER)
            .items(Arrays.asList(itemDto))
            .build();
    }

    @Nested
    @DisplayName("Testes de Operações de Busca")
    class FindOperationsTests {
        
        @Test
        @DisplayName("Deve retornar todas as compras com sucesso")
        void getAll_ShouldReturnAllShops() {
            // Arrange
            List<Shop> shops = Arrays.asList(shop);
            when(shopRepository.findAll()).thenReturn(shops);
            when(mapper.toResponse(any(Shop.class))).thenReturn(shopResponseDto);

            // Act
            List<ShopResponseDto> result = shopService.getAll();

            // Assert
            assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .element(0)
                .satisfies(response -> {
                    assertThat(response.getId()).isEqualTo(SHOP_ID);
                    assertThat(response.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
                    assertThat(response.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
                });
                
            verify(shopRepository).findAll();
            verify(mapper, times(1)).toResponse(any(Shop.class));
        }

        @Test
        @DisplayName("Deve retornar página de compras com sucesso")
        void getAllPage_ShouldReturnShopsPage() {
            // Arrange
            PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
            List<Shop> shops = Arrays.asList(shop);
            Page<Shop> shopPage = new PageImpl<>(shops, pageRequest, shops.size());
            when(shopRepository.findAll(pageRequest)).thenReturn(shopPage);
            when(mapper.toResponse(any(Shop.class))).thenReturn(shopResponseDto);

            // Act
            Page<ShopResponseDto> result = shopService.getAllPage(pageRequest);

            // Assert
            assertThat(result)
                .isNotEmpty()
                .hasSize(1);
            assertThat(result.getNumber()).isEqualTo(PAGE_NUMBER);
            assertThat(result.getSize()).isEqualTo(PAGE_SIZE);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent())
                .element(0)
                .satisfies(response -> {
                    assertThat(response.getId()).isEqualTo(SHOP_ID);
                    assertThat(response.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
                    assertThat(response.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
                });
            
            verify(shopRepository).findAll(pageRequest);
            verify(mapper, times(1)).toResponse(any(Shop.class));
        }

        @Test
        @DisplayName("Deve retornar compra por id com sucesso")
        void findById_ShouldReturnShop() {
            // Arrange
            LocalDateTime shopDate = LocalDateTime.now();
            shop.setDate(shopDate);
            shopResponseDto.setDate(shopDate);
            
            when(shopRepository.findById(SHOP_ID)).thenReturn(Optional.of(shop));
            when(mapper.toResponse(shop)).thenReturn(shopResponseDto);

            // Act
            ShopResponseDto result = shopService.findById(SHOP_ID);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(SHOP_ID);
            assertThat(result.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
            assertThat(result.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
            assertThat(result.getDate()).isEqualTo(shopDate);
            
            verify(shopRepository).findById(SHOP_ID);
            verify(mapper).toResponse(shop);
        }

        @Test
        @DisplayName("Deve lançar exceção quando compra não for encontrada por id")
        void findById_ShouldThrowException_WhenShopNotFound() {
            // Arrange
            when(shopRepository.findById(SHOP_ID)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> shopService.findById(SHOP_ID))
                .isInstanceOf(ShoppingNotFoundException.class)
                .hasMessage(SHOP_NOT_FOUND_MESSAGE + SHOP_ID + "'");
                
            verify(shopRepository).findById(SHOP_ID);
        }
    }

    @Nested
    @DisplayName("Testes de Operações de Salvar")
    class SaveOperationsTests {

        @Test
        @DisplayName("Deve salvar compra com sucesso")
        void save_ShouldSaveShop() {
            // Arrange
            ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .productIdentifier(PRODUCT_IDENTIFIER)
                .price(PRODUCT_PRICE)
                .build();
            
            UserResponseDto userResponseDto = UserResponseDto.builder()
                .cpf(USER_IDENTIFIER)
                .build();

            LocalDateTime currentTime = LocalDateTime.now();
            shop.setDate(currentTime);
            shopResponseDto.setDate(currentTime);
            
            // Configurar o mock para setar a data quando o fromRequest for chamado
            doAnswer(invocation -> {
                Shop shopResult = shop;
                shopResult.setDate(currentTime);
                shopResult.setTotal(SHOP_TOTAL);
                return shopResult;
            }).when(mapper).fromRequest(any(ShopRequestDto.class));

            when(userService.getUserByCpf(USER_IDENTIFIER)).thenReturn(userResponseDto);
            when(productService.getProductByIdentifier(PRODUCT_IDENTIFIER)).thenReturn(productResponseDto);
            when(mapper.toResponse(any(Shop.class))).thenReturn(shopResponseDto);
            when(shopRepository.save(any(Shop.class))).thenReturn(shop);

            // Act
            ShopResponseDto result = shopService.save(shopRequestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(SHOP_ID);
            assertThat(result.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
            assertThat(result.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
            assertThat(result.getDate()).isEqualTo(currentTime);
            
            verify(userService).getUserByCpf(USER_IDENTIFIER);
            verify(productService).getProductByIdentifier(PRODUCT_IDENTIFIER);
            verify(mapper).fromRequest(shopRequestDto);
            verify(shopRepository).save(any(Shop.class));
            verify(mapper).toResponse(shop);
        }

        @Test
        @DisplayName("Deve retornar nulo quando usuário não for encontrado")
        void save_ShouldReturnNull_WhenUserNotFound() {
            // Arrange
            when(userService.getUserByCpf(anyString())).thenReturn(null);

            // Act
            ShopResponseDto result = shopService.save(shopRequestDto);

            // Assert
            assertThat(result).isNull();
            verify(shopRepository, never()).save(any(Shop.class));
        }
    }

    @Nested
    @DisplayName("Testes de Operações de Exclusão")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Deve excluir compra com sucesso")
        void delete_ShouldDeleteShop() {
            // Arrange
            when(shopRepository.existsById(SHOP_ID)).thenReturn(true);

            // Act
            shopService.delete(SHOP_ID);

            // Assert
            verify(shopRepository).deleteById(SHOP_ID);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar excluir compra inexistente")
        void delete_ShouldThrowException_WhenShopNotFound() {
            // Arrange
            when(shopRepository.existsById(SHOP_ID)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> shopService.delete(SHOP_ID))
                .isInstanceOf(ShoppingNotFoundException.class)
                .hasMessage(SHOP_NOT_FOUND_MESSAGE + SHOP_ID + "'");
                
            verify(shopRepository, never()).deleteById(SHOP_ID);
        }
    }

    @Nested
    @DisplayName("Testes Relacionados ao Usuário")
    class UserRelatedTests {
        
        @Test
        @DisplayName("Deve retornar compras por identificador de usuário")
        void getByUser_ShouldReturnUserShops() {
            // Arrange
            LocalDateTime shopDate = LocalDateTime.now();
            shop.setDate(shopDate);
            shopResponseDto.setDate(shopDate);
            
            List<Shop> shops = Arrays.asList(shop);
            UserResponseDto userResponseDto = UserResponseDto.builder()
                .cpf(USER_IDENTIFIER)
                .name("José da Silva")
                .email("jose@email.com")
                .build();
            
            when(userService.getUserByCpf(USER_IDENTIFIER)).thenReturn(userResponseDto);
            when(shopRepository.findAllByUserIdentifier(USER_IDENTIFIER)).thenReturn(shops);
            when(mapper.toResponse(any(Shop.class))).thenReturn(shopResponseDto);

            // Act
            List<ShopResponseDto> result = shopService.getByUser(USER_IDENTIFIER);

            // Assert
            assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .element(0)
                .satisfies(response -> {
                    assertThat(response.getId()).isEqualTo(SHOP_ID);
                    assertThat(response.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
                    assertThat(response.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
                    assertThat(response.getDate()).isEqualTo(shopDate);
                });
            
            verify(userService).getUserByCpf(USER_IDENTIFIER);
            verify(shopRepository).findAllByUserIdentifier(USER_IDENTIFIER);
            verify(mapper).toResponse(any(Shop.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não for encontrado")
        void getByUser_ShouldThrowException_WhenUserNotFound() {
            // Arrange
            when(userService.getUserByCpf(USER_IDENTIFIER))
                .thenThrow(new ResourceNotFoundException("User", "identifier", USER_IDENTIFIER));

            // Act & Assert
            assertThatThrownBy(() -> shopService.getByUser(USER_IDENTIFIER))
                .isInstanceOf(ResourceNotFoundException.class);
                
            verify(shopRepository, never()).findAllByUserIdentifier(anyString());
        }
    }

    @Nested
    @DisplayName("Testes de Operações de Relatório")
    class ReportOperationsTests {
        
        @Test
        @DisplayName("Deve retornar compras por filtro")
        void getShopsByFilter_ShouldReturnFilteredShops() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            LocalDateTime shopDate = LocalDateTime.now();
            
            shop.setDate(shopDate);
            shopResponseDto.setDate(shopDate);
            
            List<Shop> shops = Arrays.asList(shop);
            
            when(reportRepository.getShopByFilters(startDate, endDate, MAX_VALUE)).thenReturn(shops);
            when(mapper.toResponse(any(Shop.class))).thenReturn(shopResponseDto);

            // Act
            List<ShopResponseDto> result = shopService.getShopsByFilter(startDate, endDate, MAX_VALUE);

            // Assert
            assertThat(result)
                .isNotEmpty()
                .hasSize(1)
                .element(0)
                .satisfies(response -> {
                    assertThat(response.getId()).isEqualTo(SHOP_ID);
                    assertThat(response.getUserIdentifier()).isEqualTo(USER_IDENTIFIER);
                    assertThat(response.getTotal()).isEqualByComparingTo(SHOP_TOTAL);
                    assertThat(response.getDate()).isEqualTo(shopDate);
                });
            
            verify(reportRepository).getShopByFilters(startDate, endDate, MAX_VALUE);
            verify(mapper).toResponse(any(Shop.class));
        }

        @Test
        @DisplayName("Deve retornar relatório por data")
        void getReportByDate_ShouldReturnReport() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            
            // Criando um relatório com valores específicos para testar
            BigDecimal reportTotal = new BigDecimal("1250.75");
            BigDecimal reportMean = new BigDecimal("416.92");
            Integer reportCount = 3;
            
            ShopReportResponseDto expectedReport = ShopReportResponseDto.builder()
                .count(reportCount)
                .total(reportTotal)
                .mean(reportMean)
                .build();
            
            when(reportRepository.getReportByDate(startDate, endDate)).thenReturn(expectedReport);

            // Act
            ShopReportResponseDto result = shopService.getReportByDate(startDate, endDate);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCount()).isEqualTo(reportCount);
            assertThat(result.getTotal()).isEqualByComparingTo(reportTotal);
            assertThat(result.getMean()).isEqualByComparingTo(reportMean);
            
            verify(reportRepository).getReportByDate(startDate, endDate);
        }
    }
}