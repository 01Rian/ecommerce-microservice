package com.ecommerce.productapi.productapi.services;

import com.ecommerce.productapi.productapi.domain.dto.CategoryDto;
import com.ecommerce.productapi.productapi.domain.entities.CategoryEntity;
import com.ecommerce.productapi.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.productapi.mappers.impl.CategoryMapper;
import com.ecommerce.productapi.productapi.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        CategoryEntity category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        return mapper.mapTo(category);
    }

    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setNome(categoryDto.getNome().toLowerCase());
        CategoryEntity category = categoryRepository.save(mapper.mapFrom(categoryDto));

        return mapper.mapTo(category);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        Optional<CategoryEntity> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isEmpty()) {
            throw new CategoryNotFoundException();
        }

        existingCategory.get().setNome(categoryDto.getNome());

        CategoryEntity updateCategory = categoryRepository.save(existingCategory.get());
        return mapper.mapTo(updateCategory);
    }

    @Transactional
    public void deleteById(Long id) throws CategoryNotFoundException {
        CategoryEntity category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.deleteById(id);
    }
}
