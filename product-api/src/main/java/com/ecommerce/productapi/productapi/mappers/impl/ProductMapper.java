package com.ecommerce.productapi.productapi.mappers.impl;

import com.ecommerce.productapi.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.productapi.domain.entities.ProductEntity;
import com.ecommerce.productapi.productapi.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements Mapper<ProductEntity, ProductDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDto mapTo(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public ProductEntity mapFrom(ProductDto productDto) {
        return modelMapper.map(productDto, ProductEntity.class);
    }
}
