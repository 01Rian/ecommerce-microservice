package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryMapper implements Mapper<Category, CategoryRequest, CategoryResponse> {

    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse toResponse(Category category) {
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public Category toEntity(CategoryRequest request) {
        return modelMapper.map(request, Category.class);
    }
}
