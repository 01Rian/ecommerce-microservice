package com.ecommerce.userapi.mapper.impl;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.domain.entity.UserEntity;
import com.ecommerce.userapi.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
