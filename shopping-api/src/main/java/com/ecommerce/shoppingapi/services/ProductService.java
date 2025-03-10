package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    
    private final WebClient webClient;
    
    public ProductService(WebClient.Builder webClientBuilder, 
                         @Value("${product.api.url:http://product-api:8081/api/v1}") String productApiUrl) {
        this.webClient = webClientBuilder
            .baseUrl(productApiUrl)
            .build();
    }

    public ProductResponseDto getProductByIdentifier(String productIdentifier) {
        try {
            Mono<ProductResponseDto> productMono = webClient.get()
                    .uri("/products/" + productIdentifier)
                    .retrieve()
                    .bodyToMono(ProductResponseDto.class);

            ProductResponseDto product = productMono.block();

            if (product == null) {
                throw new ResourceNotFoundException("Produto não encontrado");
            }
            return product;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
    }
}
