package com.ecommerce.userapi.mapper;

import com.ecommerce.userapi.domain.dto.UserDto;
import com.ecommerce.userapi.domain.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MapperImpl implements Mapper<UserEntity, UserDto> {

    private ModelMapper modelMapper;

    public MapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}
