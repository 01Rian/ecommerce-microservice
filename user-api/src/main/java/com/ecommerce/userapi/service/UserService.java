package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAll() {
        return null;
    }

    public UserDto findById(long userId) {
        return null;
    }

    public UserDto save(UserDto userDto) {
        return null;
    }

    public UserDto delete(long userId) {
        return null;
    }

    public UserDto findByCpf(String cpf) {
        return null;
    }

    public List<UserDto> queryByName(String name) {
        return null;
    }
}
