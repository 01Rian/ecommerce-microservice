package com.ecommerce.productapi.productapi.services;

import com.ecommerce.productapi.productapi.domain.dto.CategoryDto;
import com.ecommerce.productapi.productapi.domain.entities.CategoryEntity;
import com.ecommerce.productapi.productapi.mappers.impl.CategoryMapper;
import com.ecommerce.productapi.productapi.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper mapper;

    public List<CategoryDto> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(long id) {
        Optional<CategoryEntity> category = categoryRepository.findById(id);
        return mapper.mapTo(category.get());
    }

    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setNome(categoryDto.getNome().toLowerCase());

        CategoryEntity category = categoryRepository.save(mapper.mapFrom(categoryDto));
        return  mapper.mapTo(category);
    }

    public void deleteById(long id) {
        categoryRepository.deleteById(id);
    }
}
