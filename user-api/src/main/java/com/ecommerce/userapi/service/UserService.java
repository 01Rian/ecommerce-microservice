package com.ecommerce.userapi.service;

import com.ecommerce.userapi.domain.dto.UserDto;
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
    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllPage(PageRequest page) {
        Page<User> users = userRepository.findAll(page);
        return users.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return mapper.mapTo(user);
    }

    @Transactional(readOnly = true)
    public UserDto findByCpf(String cpf) {
        User user = userRepository.findByCpf(cpf);
        if (user != null) {
            return mapper.mapTo(user);
        }
        throw new UserNotFoundException();
    }

    @Transactional(readOnly = true)
    public List<UserDto> queryByName(String name) {
        List<User> users = userRepository.queryByNameLike(name);
        return users.stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto save(UserDto userDto) {
        User ifExist = userRepository.findByCpf(userDto.getCpf());

        if (ifExist != null) {
            throw new UserAlreadyExistsException();
        }

        userDto.setName(userDto.getName().toLowerCase());
        userDto.setDataRegister(LocalDateTime.now());
        User user = userRepository.save(mapper.mapFrom(userDto));

        return mapper.mapTo(user);
    }

    @Transactional
    public UserDto update(UserDto userDto, String cpf) {
        User existingUser = userRepository.findByCpf(cpf);

        if (existingUser == null) {
            throw new UserNotFoundException();
        }

        setFields(userDto, existingUser);

        User updatedUser = userRepository.save(existingUser);
        return mapper.mapTo(updatedUser);
    }

    private static void setFields(UserDto userDto, User existingUser) {
        existingUser.setCpf(Objects.requireNonNullElse(userDto.getCpf(), existingUser.getCpf()));
        existingUser.setName(Objects.requireNonNullElse(userDto.getName(), existingUser.getName().toLowerCase()));
        existingUser.setAddress(Objects.requireNonNullElse(userDto.getAddress(), existingUser.getAddress()));
        existingUser.setEmail(Objects.requireNonNullElse(userDto.getEmail(), existingUser.getEmail()));
        existingUser.setPhone(Objects.requireNonNullElse(userDto.getPhone(), existingUser.getPhone()));
        existingUser.setDataRegister(Objects.requireNonNullElse(userDto.getDataRegister(), existingUser.getDataRegister()));
    }

    @Transactional
    public void delete(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }
}
