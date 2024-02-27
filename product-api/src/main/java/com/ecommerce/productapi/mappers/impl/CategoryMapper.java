package com.ecommerce.productapi.mappers.impl;

import com.ecommerce.productapi.domain.dto.CategoryDto;
import com.ecommerce.productapi.mappers.Mapper;
import com.ecommerce.productapi.domain.entities.Category;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CategoryMapper implements Mapper<Category, CategoryDto> {

    private final ModelMapper modelMapper;

    @Override
    public CategoryDto mapTo(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public Category mapFrom(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}
