package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserResponseDto;
import com.ecommerce.shoppingapi.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    public UserResponseDto getUserByCfp(String cpf) {

        try {
            String productApi = "http://user-api:8080/api/v1/users";
            //String productApi = "http://localhost:8080/api/v1/users";

            WebClient webClient = WebClient.builder()
                    .baseUrl(productApi)
                    .build();

            Mono<UserResponseDto> user = webClient.get()
                    .uri("/cpf/" + cpf)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class);

            return user.block();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
    }
}
