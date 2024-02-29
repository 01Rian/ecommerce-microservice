package com.ecommerce.shoppingapi.services;

import com.ecommerce.shoppingapi.domain.dto.user.UserDto;
import com.ecommerce.shoppingapi.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

    public UserDto getUserByCfp(String cpf) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "http://localhost:8080/api/users/cpf/" + cpf;
            ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);

            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException();
        }
    }
}
