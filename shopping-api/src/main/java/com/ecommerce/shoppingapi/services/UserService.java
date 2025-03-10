package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    
    private final WebClient webClient;
    
    public UserService(WebClient.Builder webClientBuilder,
                      @Value("${user.api.url:http://user-api:8080/api/v1/users}") String userApiUrl) {
        this.webClient = webClientBuilder
            .baseUrl(userApiUrl)
            .build();
    }

    public UserResponseDto getUserByCpf(String cpf) {
        try {
            Mono<UserResponseDto> userMono = webClient.get()
                    .uri("/cpf/" + cpf)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class);

            UserResponseDto user = userMono.block();
            
            if (user == null) {
                throw new ResourceNotFoundException("Usuário não encontrado");
            }
            
            return user;
        } catch (Exception e) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
    }
}
