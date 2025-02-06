package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.domain.entity.User;
import com.ecommerce.userapi.exception.UserAlreadyExistsException;
import com.ecommerce.userapi.exception.UserNotFoundException;
import com.ecommerce.userapi.mapper.impl.MapperImpl;
import com.ecommerce.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MapperImpl mapper;

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDto> findByPage(PageRequest page) {
        Page<User> users = userRepository.findAll(page);
        return users.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("id", userId));
        return mapper.mapTo(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf);
        if (user != null) {
            return mapper.mapTo(user);
        }
        throw new UserNotFoundException("cpf", cpf);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findByQueryName(String name) {
        List<User> users = userRepository.queryByNameLike(name);
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto save(UserRequestDto userRequestDto) {
        User ifExist = userRepository.findByCpf(userRequestDto.getCpf());

        if (ifExist != null) {
            throw new UserAlreadyExistsException("cpf", userRequestDto.getCpf());
        }

        User user = mapper.mapFrom(userRequestDto);
        user.setName(user.getName().toLowerCase());
        user.setDataRegister(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        return mapper.mapTo(savedUser);
    }

    @Transactional
    public UserResponseDto update(UserRequestDto userRequestDto, String cpf) {
        User existingUser = userRepository.findByCpf(cpf);

        if (existingUser == null) {
            throw new UserNotFoundException("cpf", cpf);
        }

        setFields(userRequestDto, existingUser);

        User updatedUser = userRepository.save(existingUser);
        return mapper.mapTo(updatedUser);
    }

    private static void setFields(UserRequestDto userRequestDto, User existingUser) {
        existingUser.setCpf(Objects.requireNonNullElse(userRequestDto.getCpf(), existingUser.getCpf()));
        existingUser.setName(Objects.requireNonNullElse(userRequestDto.getName(), existingUser.getName()).toLowerCase());
        existingUser.setAddress(Objects.requireNonNullElse(userRequestDto.getAddress(), existingUser.getAddress()));
        existingUser.setEmail(Objects.requireNonNullElse(userRequestDto.getEmail(), existingUser.getEmail()));
        existingUser.setPhone(Objects.requireNonNullElse(userRequestDto.getPhone(), existingUser.getPhone()));
    }

    @Transactional
    public void delete(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("id", userId));
        userRepository.delete(user);
    }
}
