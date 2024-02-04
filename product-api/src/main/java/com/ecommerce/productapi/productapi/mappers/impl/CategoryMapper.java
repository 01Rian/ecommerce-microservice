package com.ecommerce.productapi.productapi.mappers.impl;

import com.ecommerce.productapi.productapi.domain.dto.CategoryDto;
import com.ecommerce.productapi.productapi.domain.entities.CategoryEntity;
import com.ecommerce.productapi.productapi.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<CategoryEntity, CategoryDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryDto mapTo(CategoryEntity categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryDto.class);
    }

    @Override
    public CategoryEntity mapFrom(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, CategoryEntity.class);
    }
}
