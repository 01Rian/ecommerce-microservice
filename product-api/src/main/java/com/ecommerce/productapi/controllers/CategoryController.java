package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.request.CategoryRequest;
import com.ecommerce.productapi.domain.dto.response.CategoryResponse;
import com.ecommerce.productapi.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAllCategories() {
        List<CategoryResponse> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findCategoryById(@PathVariable("id") Long id) {
        CategoryResponse category = categoryService.findCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> newCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse newCategory = categoryService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Valid @RequestBody CategoryRequest request,
            @PathVariable("id") Long id) {
        CategoryResponse updatedCategory = categoryService.update(id, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
