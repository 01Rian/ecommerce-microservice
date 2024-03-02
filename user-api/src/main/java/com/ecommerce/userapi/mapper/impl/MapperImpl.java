package com.ecommerce.userapi.mapper.impl;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.domain.entity.User;
import com.ecommerce.userapi.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MapperImpl implements Mapper<User, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto mapTo(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
