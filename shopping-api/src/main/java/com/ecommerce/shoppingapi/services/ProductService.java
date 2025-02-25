package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    public ProductResponseDto getProductByIdentifier(String productIdentifier) {

        try {
            String productApi = "http://product-api:8081/api/v1";

            WebClient webClient = WebClient.builder()
                    .baseUrl(productApi)
                    .build();

            Mono<ProductResponseDto> product = webClient.get()
                    .uri("/products/" + productIdentifier)
                    .retrieve()
                    .bodyToMono(ProductResponseDto.class);

            return product.block();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }
}
