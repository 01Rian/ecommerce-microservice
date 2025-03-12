package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final String PRODUCT_IDENTIFIER = "PROD-001";
    private static final Long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "Test Product";
    private static final String PRODUCT_DESCRIPTION = "Test Description";
    private static final BigDecimal PRODUCT_PRICE = new BigDecimal("99.99");
    private static final Integer PRODUCT_QUANTITY = 10;
    private static final Long CATEGORY_ID = 1L;
    private static final String CATEGORY_NAME = "Test Category";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Produto não encontrado";
    private static final String API_ERROR_MESSAGE = "API Error";
    private static final String URI_PATH_PRODUCTS = "/products/";
    private static final String PRODUCT_API_URL = "http://product-api:8081/api/v1";
    
    private ProductService productService;
    
    @Mock
    private WebClient webClientMock;
    
    @Mock
    private WebClient.Builder webClientBuilderMock;
    
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    
    @Mock
    private WebClient.ResponseSpec responseSpecMock;
    
    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        // Configurar o mock do WebClient.Builder
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);
        
        // Configurar o mock do WebClient
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        
        // Criar uma instância real do ProductService com o WebClient.Builder mockado
        productService = new ProductService(webClientBuilderMock, PRODUCT_API_URL);
    }

    @Nested
    @DisplayName("Testes para o método getProductByIdentifier")
    class GetProductByIdentifierTests {

        @Test
        @DisplayName("getProductByIdentifier_Identificador_Válido_RetornaProdutoResponseDto")
        void getProductByIdentifier_ValidIdentifier_ReturnsProductResponseDto() {
            // Arrange            
            ProductResponseDto expectedProduct = ProductResponseDto.builder()
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

            when(responseSpecMock.bodyToMono(ProductResponseDto.class)).thenReturn(Mono.just(expectedProduct));

            // Act
            ProductResponseDto actualProduct = productService.getProductByIdentifier(PRODUCT_IDENTIFIER);

            // Assert
            assertThat(actualProduct).isNotNull();
            assertThat(actualProduct.getId()).isEqualTo(PRODUCT_ID);
            assertThat(actualProduct.getName()).isEqualTo(PRODUCT_NAME);
            assertThat(actualProduct.getDescription()).isEqualTo(PRODUCT_DESCRIPTION);
            assertThat(actualProduct.getPrice()).isEqualTo(PRODUCT_PRICE);
            assertThat(actualProduct.getQuantity()).isEqualTo(PRODUCT_QUANTITY);
            assertThat(actualProduct.getProductIdentifier()).isEqualTo(PRODUCT_IDENTIFIER);
            assertThat(actualProduct.getCategoryId()).isEqualTo(CATEGORY_ID);
            assertThat(actualProduct.getCategoryName()).isEqualTo(CATEGORY_NAME);
            
            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_PRODUCTS + PRODUCT_IDENTIFIER);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(ProductResponseDto.class);
        }

        @Test
        @DisplayName("getProductByIdentifier_Erro_WebClient_LançaResourceNotFoundException")
        void getProductByIdentifier_WebClientError_ThrowsResourceNotFoundException() {
            // Arrange
            Exception webClientException = new RuntimeException(API_ERROR_MESSAGE);

            when(responseSpecMock.bodyToMono(ProductResponseDto.class)).thenReturn(Mono.error(webClientException));

            // Act & Assert
            assertThatThrownBy(() -> productService.getProductByIdentifier(PRODUCT_IDENTIFIER))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(PRODUCT_NOT_FOUND_MESSAGE);

            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_PRODUCTS + PRODUCT_IDENTIFIER);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(ProductResponseDto.class);
        }

        @Test
        @DisplayName("getProductByIdentifier_Resposta_Nula_LançaResourceNotFoundException")
        void getProductByIdentifier_NullResponse_ThrowsResourceNotFoundException() {
            // Arrange
            when(responseSpecMock.bodyToMono(ProductResponseDto.class)).thenReturn(Mono.empty());

            // Act & Assert
            assertThatThrownBy(() -> productService.getProductByIdentifier(PRODUCT_IDENTIFIER))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage(PRODUCT_NOT_FOUND_MESSAGE);
                    
            verify(webClientMock).get();
            verify(requestHeadersUriSpecMock).uri(URI_PATH_PRODUCTS + PRODUCT_IDENTIFIER);
            verify(requestHeadersSpecMock).retrieve();
            verify(responseSpecMock).bodyToMono(ProductResponseDto.class);
        }
    }
}