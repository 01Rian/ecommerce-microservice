package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductDto;
import com.ecommerce.shoppingapi.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    public ProductDto getProductByIdentifier(String productIdentifier) {

        try {
            String productApi = "http://product-api:8081/api/v1";

            WebClient webClient = WebClient.builder()
                    .baseUrl(productApi)
                    .build();

            Mono<ProductDto> product = webClient.get()
                    .uri("/products/" + productIdentifier)
                    .retrieve()
                    .bodyToMono(ProductDto.class);

            return product.block();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProductNotFoundException();
        }
    }
}
