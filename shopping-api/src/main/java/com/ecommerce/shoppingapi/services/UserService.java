package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    
    private WebClient.Builder webClientBuilder;
    
    public UserService() {
        this.webClientBuilder = WebClient.builder();
    }

    public UserResponseDto getUserByCfp(String cpf) {
        try {
            String userApi = "http://user-api:8080/api/v1/users";
            //String userApi = "http://localhost:8080/api/v1/users";

            WebClient webClient = webClientBuilder
                    .baseUrl(userApi)
                    .build();

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
            e.printStackTrace();
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
    }
}
