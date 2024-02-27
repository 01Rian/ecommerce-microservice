package com.ecommerce.productapi.services;

import com.ecommerce.productapi.domain.dto.CategoryDto;
import com.ecommerce.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.mappers.impl.CategoryMapper;
import com.ecommerce.productapi.domain.entities.Category;
import com.ecommerce.productapi.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        return mapper.mapTo(category);
    }

    @Transactional
    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setName(categoryDto.getName().toLowerCase());
        Category category = categoryRepository.save(mapper.mapFrom(categoryDto));

        return mapper.mapTo(category);
    }

    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        Optional<Category> existingCategory = categoryRepository.findById(id);

        if (existingCategory.isEmpty()) {
            throw new CategoryNotFoundException();
        }

        existingCategory.get().setName(categoryDto.getName());

        Category updateCategory = categoryRepository.save(existingCategory.get());
        return mapper.mapTo(updateCategory);
    }

    @Transactional
    public void deleteById(Long id) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.deleteById(id);
    }
}
