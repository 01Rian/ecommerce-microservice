package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.domain.entity.UserEntity;
import com.ecommerce.userapi.mapper.MapperImpl;
import com.ecommerce.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MapperImpl mapper;

    public List<UserDto> getAll() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long userId) {
        Optional<UserEntity> users = userRepository.findById(userId);
        return users.map(userEntity -> mapper.mapTo(userEntity)).orElse(null);
    }

    public UserDto save(UserDto userDto) {
        userDto.setNome(userDto.getNome().toLowerCase());
        UserEntity user = userRepository.save(mapper.mapFrom(userDto));
        return mapper.mapTo(user);
    }

    public void delete(long userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        user.ifPresent(userEntity -> userRepository.delete(userEntity));
    }

    public UserDto findByCpf(String cpf) {
        UserEntity user = userRepository.findByCpf(cpf);
        if (user != null) {
            return mapper.mapTo(user);
        }
        return null;
    }

    public List<UserDto> queryByName(String name) {
        List<UserEntity> users = userRepository.queryByNomeLike(name);
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }
}
