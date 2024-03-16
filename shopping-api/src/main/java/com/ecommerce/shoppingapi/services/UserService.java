package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserDto;
import com.ecommerce.shoppingapi.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    public UserDto getUserByCfp(String cpf) {

        try {
            String productApi = "http://user-api:8080/api/users";

            WebClient webClient = WebClient.builder()
                    .baseUrl(productApi)
                    .build();

            Mono<UserDto> user = webClient.get()
                    .uri("/cpf/" + cpf)
                    .retrieve()
                    .bodyToMono(UserDto.class);

            return user.block();
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserNotFoundException();
        }
    }
}
