package com.ecommerce.shoppingapi.mappers.impl;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.entities.ShopEntity;
import com.ecommerce.shoppingapi.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopMapper implements Mapper<ShopEntity, ShopDto> {

    private final ModelMapper modelMapper;

    @Override
    public ShopDto mapTo(ShopEntity shopEntity) {
        return modelMapper.map(shopEntity, ShopDto.class);
    }

    @Override
    public ShopEntity mapFrom(ShopDto shopDto) {
        return modelMapper.map(shopDto, ShopEntity.class);
    }
}
