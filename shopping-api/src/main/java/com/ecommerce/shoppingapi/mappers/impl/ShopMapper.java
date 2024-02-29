package com.ecommerce.shoppingapi.mappers.impl;

import com.ecommerce.shoppingapi.domain.dto.ShopDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import com.ecommerce.shoppingapi.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopMapper implements Mapper<Shop, ShopDto> {

    private final ModelMapper modelMapper;

    @Override
    public ShopDto mapTo(Shop shop) {
        return modelMapper.map(shop, ShopDto.class);
    }

    @Override
    public Shop mapFrom(ShopDto shopDto) {
        return modelMapper.map(shopDto, Shop.class);
    }
}
