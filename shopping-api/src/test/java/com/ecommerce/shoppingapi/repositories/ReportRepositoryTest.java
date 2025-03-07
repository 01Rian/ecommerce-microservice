package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Item;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do ReportRepository")
class ReportRepositoryTest {

    private final ShopRepository shopRepository;
    private final TestEntityManager entityManager;

    private Shop shop1;
    private Shop shop2;
    
    @Autowired
    public ReportRepositoryTest(ShopRepository shopRepository, TestEntityManager entityManager) {
        this.shopRepository = shopRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Criação e persistência das compras para os testes
        shop1 = createAndPersistShop("12345678901", LocalDateTime.now().minusDays(1), new BigDecimal("100.00"));
        shop2 = createAndPersistShop("12345678901", LocalDateTime.now().minusDays(3), new BigDecimal("200.00"));
        createAndPersistShop("98765432109", LocalDateTime.now().minusDays(5), new BigDecimal("300.00"));
    }

    @Nested
    @DisplayName("Testes de filtro de compras")
    class ShopFilterTests {
        
        @Test
        @DisplayName("Deve retornar compras quando aplicar filtro por data")
        void shouldReturnShops_WhenFilterByDate() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(4);
            
            // Act
            List<Shop> result = shopRepository.getShopByFilters(startDate, null, null);

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .extracting(Shop::getId)
                    .containsExactlyInAnyOrder(shop1.getId(), shop2.getId());
        }

        @Test
        @DisplayName("Deve retornar compras quando aplicar filtro por data e valor máximo")
        void shouldReturnShops_WhenFilterByDateAndMaxValue() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(6);
            BigDecimal maxValue = new BigDecimal("150.00");
            
            // Act
            List<Shop> result = shopRepository.getShopByFilters(startDate, null, maxValue);

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .element(0)
                    .satisfies(shop -> {
                        assertThat(shop.getId()).isEqualTo(shop1.getId());
                        assertThat(shop.getTotal()).isEqualByComparingTo(new BigDecimal("100.00"));
                    });
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não encontrar compras com os filtros")
        void shouldReturnEmptyList_WhenNoShopsMatchFilters() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(10);
            LocalDate endDate = LocalDate.now().minusDays(6);
            
            // Act
            List<Shop> result = shopRepository.getShopByFilters(startDate, endDate, null);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de relatório de compras")
    class ShopReportTests {

        @Test
        @DisplayName("Deve retornar relatório correto quando filtrar por período")
        void shouldReturnCorrectReport_WhenFilterByPeriod() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(6);
            LocalDate endDate = LocalDate.now();
            
            // Act
            ShopReportResponseDto result = shopRepository.getReportByDate(startDate, endDate);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCount()).isEqualTo(3);
            assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("600.00"));
            
            // Média dos valores (100 + 200 + 300) / 3 = 200
            assertThat(result.getMean()).isEqualByComparingTo(new BigDecimal("200.00"));
        }

        @Test
        @DisplayName("Deve retornar relatório com valores zerados quando não encontrar compras no período")
        void shouldReturnZeroValues_WhenNoShopsInPeriod() {
            // Arrange
            LocalDate startDate = LocalDate.now().minusDays(30);
            LocalDate endDate = LocalDate.now().minusDays(10);
            
            // Act
            ShopReportResponseDto result = shopRepository.getReportByDate(startDate, endDate);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getCount()).isEqualTo(0);
            assertThat(result.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getMean()).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    private Shop createAndPersistShop(String userIdentifier, LocalDateTime date, BigDecimal total) {
        Item item = Item.builder()
                .productIdentifier("PROD-" + System.currentTimeMillis())
                .price(total)
                .build();

        Shop shop = Shop.builder()
                .userIdentifier(userIdentifier)
                .total(total)
                .date(date)
                .items(Arrays.asList(item))
                .build();

        return entityManager.persist(shop);
    }
}