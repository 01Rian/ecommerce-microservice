package com.ecommerce.shoppingapi.integration;

import com.ecommerce.shoppingapi.config.TestConfig;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("Testes de Integração - Base")
public abstract class BaseIntegrationTest {

    protected static final int WIREMOCK_PORT = 8081;

    protected WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(
            wireMockConfig().port(WIREMOCK_PORT)
        );
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}