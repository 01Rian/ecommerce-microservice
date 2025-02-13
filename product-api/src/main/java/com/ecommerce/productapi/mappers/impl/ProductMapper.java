package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.domain.entities.Product;
import com.ecommerce.productapi.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProductMapper implements Mapper<Product, ProductRequest, ProductResponse> {

    private final ModelMapper modelMapper;

    @Override
    public ProductResponse toResponse(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }

    @Override
    public Product toEntity(ProductRequest request) {
        return modelMapper.map(request, Product.class);
    }
}
