package com.ecommerce.shoppingapi.mappers.impl;

import com.ecommerce.shoppingapi.domain.dto.shop.ShopRequestDto;
import com.ecommerce.shoppingapi.domain.dto.shop.ShopResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import com.ecommerce.shoppingapi.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopMapper implements Mapper<Shop, ShopRequestDto, ShopResponseDto> {

    private final ModelMapper modelMapper;

    @Override
    public ShopResponseDto toResponse(Shop shop) {
        return modelMapper.map(shop, ShopResponseDto.class);
    }

    @Override
    public Shop fromRequest(ShopRequestDto shopDto) {
        return modelMapper.map(shopDto, Shop.class);
    }
}
