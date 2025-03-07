package com.ecommerce.shoppingapi.repositories;

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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes do ShopRepository")
class ShopRepositoryTest {

    private static final String USER_IDENTIFIER_1 = "12345678901";
    private static final String USER_IDENTIFIER_2 = "98765432109";
    private static final String NON_EXISTENT_USER = "00000000000";

    private final ShopRepository shopRepository;
    private final TestEntityManager entityManager;

    private Shop shop1;
    private Shop shop2;

    @Autowired
    public ShopRepositoryTest(ShopRepository shopRepository, TestEntityManager entityManager) {
        this.shopRepository = shopRepository;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        // Criação e persistência de compras para os testes
        shop1 = createAndPersistShop(USER_IDENTIFIER_1, LocalDateTime.now(), new BigDecimal("100.00"));
        shop2 = createAndPersistShop(USER_IDENTIFIER_2, LocalDateTime.now().minusDays(1), new BigDecimal("200.00"));
    }

    @Nested
    @DisplayName("Testes de busca por identificador de usuário")
    class FindByUserIdentifierTests {
        
        @Test
        @DisplayName("Deve retornar lista de compras quando buscar por identificador de usuário válido")
        void shouldReturnShopList_WhenSearchingByValidUserIdentifier() {
            // Act
            List<Shop> result = shopRepository.findAllByUserIdentifier(USER_IDENTIFIER_1);

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .element(0)
                    .satisfies(shop -> {
                        assertThat(shop.getUserIdentifier()).isEqualTo(USER_IDENTIFIER_1);
                        assertThat(shop.getTotal()).isEqualByComparingTo(new BigDecimal("100.00"));
                    });
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando buscar por identificador de usuário que não existe")
        void shouldReturnEmptyList_WhenSearchingByNonExistentUserIdentifier() {
            // Act
            List<Shop> result = shopRepository.findAllByUserIdentifier(NON_EXISTENT_USER);

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de persistência")
    class PersistenceTests {

        @Test
        @DisplayName("Deve salvar compra com sucesso")
        void shouldSaveShop_Successfully() {
            // Arrange
            Shop newShop = createShopEntity(USER_IDENTIFIER_1, LocalDateTime.now(), new BigDecimal("300.00"));

            // Act
            Shop savedShop = shopRepository.save(newShop);

            // Assert
            assertThat(savedShop)
                    .isNotNull()
                    .satisfies(shop -> {
                        assertThat(shop.getId()).isNotNull();
                        assertThat(shop.getUserIdentifier()).isEqualTo(USER_IDENTIFIER_1);
                        assertThat(shop.getTotal()).isEqualByComparingTo(new BigDecimal("300.00"));
                    });

            Shop foundShop = entityManager.find(Shop.class, savedShop.getId());
            assertThat(foundShop)
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(savedShop);
        }

        @Test
        @DisplayName("Deve buscar compra por ID com sucesso")
        void shouldFindShopById_Successfully() {
            // Act
            Optional<Shop> result = shopRepository.findById(shop1.getId());

            // Assert
            assertThat(result)
                    .isPresent()
                    .get()
                    .satisfies(shop -> {
                        assertThat(shop.getId()).isEqualTo(shop1.getId());
                        assertThat(shop.getUserIdentifier()).isEqualTo(USER_IDENTIFIER_1);
                        assertThat(shop.getTotal()).isEqualByComparingTo(new BigDecimal("100.00"));
                    });
        }

        @Test
        @DisplayName("Deve deletar compra com sucesso")
        void shouldDeleteShop_Successfully() {
            // Arrange
            Long shopId = shop1.getId();

            // Act
            shopRepository.deleteById(shopId);
            entityManager.flush();
            entityManager.clear();

            // Assert
            Shop foundShop = entityManager.find(Shop.class, shopId);
            assertThat(foundShop).isNull();
        }
    }

    @Nested
    @DisplayName("Testes de listagem")
    class ListTests {

        @Test
        @DisplayName("Deve retornar todas as compras")
        void shouldReturnAllShops() {
            // Act
            List<Shop> result = shopRepository.findAll();

            // Assert
            assertThat(result)
                    .isNotEmpty()
                    .hasSize(2)
                    .extracting(Shop::getId)
                    .containsExactlyInAnyOrder(shop1.getId(), shop2.getId());
        }
    }

    private Shop createAndPersistShop(String userIdentifier, LocalDateTime date, BigDecimal total) {
        Shop shop = createShopEntity(userIdentifier, date, total);
        return entityManager.persist(shop);
    }

    private Shop createShopEntity(String userIdentifier, LocalDateTime date, BigDecimal total) {
        Item item = Item.builder()
                .productIdentifier("PROD-" + System.currentTimeMillis())
                .price(total)
                .build();

        return Shop.builder()
                .userIdentifier(userIdentifier)
                .date(date)
                .total(total)
                .items(Arrays.asList(item))
                .build();
    }
}