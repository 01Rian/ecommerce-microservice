package com.ecommerce.userapi.mapper.impl;

import com.ecommerce.userapi.domain.dto.UserRequestDto;
import com.ecommerce.userapi.domain.dto.UserResponseDto;
import com.ecommerce.userapi.domain.entity.User;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MapperImpl {

    private final ModelMapper modelMapper;

    public UserResponseDto mapTo(User user) {
        return modelMapper.map(user, UserResponseDto.class);
    }

    public User mapFrom(UserRequestDto userRequestDto) {
        return modelMapper.map(userRequestDto, User.class);
    }
}
