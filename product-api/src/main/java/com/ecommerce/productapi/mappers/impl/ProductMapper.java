package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.mappers.Mapper;
import com.ecommerce.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.domain.entities.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductMapper implements Mapper<Product, ProductDto> {

    private final ModelMapper modelMapper;

    @Override
    public ProductDto mapTo(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public Product mapFrom(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
