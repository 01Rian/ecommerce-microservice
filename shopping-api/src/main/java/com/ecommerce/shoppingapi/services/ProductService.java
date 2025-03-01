package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.product.ProductResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    
    private WebClient.Builder webClientBuilder;
    
    public ProductService() {
        this.webClientBuilder = WebClient.builder();
    }

    public ProductResponseDto getProductByIdentifier(String productIdentifier) {
        try {
            String productApi = "http://product-api:8081/api/v1";
            //String productApi = "http://localhost:8081/api/v1";

            WebClient webClient = webClientBuilder
                    .baseUrl(productApi)
                    .build();

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
            e.printStackTrace();
            throw new ResourceNotFoundException("Produto não encontrado");
        }
    }
}
