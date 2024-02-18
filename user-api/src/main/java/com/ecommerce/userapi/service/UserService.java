package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.domain.entity.UserEntity;
import com.ecommerce.userapi.exception.UserBadRequestException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import com.ecommerce.userapi.mapper.impl.MapperImpl;
import com.ecommerce.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final MapperImpl mapper;

    @Autowired
    public UserService(UserRepository userRepository, MapperImpl mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllPage(PageRequest page) {
        Page<UserEntity> users = userRepository.findAll(page);
        return users.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return mapper.mapTo(user);
    }

    @Transactional
    public UserDto save(UserDto userDto) {
        UserEntity ifExist = userRepository.findByCpf(userDto.getCpf());

        if (ifExist != null) {
            throw new UserBadRequestException();
        }

        userDto.setNome(userDto.getNome().toLowerCase());
        userDto.setDataCadastro(LocalDateTime.now());
        UserEntity user = userRepository.save(mapper.mapFrom(userDto));

        return mapper.mapTo(user);
    }

    @Transactional
    public void delete(Long userId) throws UserNotFoundException {
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByCpf(String cpf) {
        UserEntity user = userRepository.findByCpf(cpf);
        if (user != null) {
            return mapper.mapTo(user);
        }
         throw new UserNotFoundException();
    }

    @Transactional(readOnly = true)
    public List<UserDto> queryByName(String name) {
        List<UserEntity> users = userRepository.queryByNomeLike(name);
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }
}
