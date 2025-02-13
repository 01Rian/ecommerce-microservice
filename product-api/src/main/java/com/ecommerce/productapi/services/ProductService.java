package com.ecommerce.productapi.services;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
import com.ecommerce.productapi.domain.entities.*;
import com.ecommerce.productapi.exception.*;
import com.ecommerce.productapi.mappers.impl.*;
import com.ecommerce.productapi.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllPageProducts(PageRequest page) {
        Page<Product> products = productRepository.findAll(page);
        return products.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProductByCategoryId(Long categoryId) {
        List<Product> products = productRepository.getProductByCategory(categoryId);
        return products.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findByProductIdentifier(String identifier) {
        Product product = productRepository.findByProductIdentifier(identifier);
        if (product == null) {
            throw new ProductNotFoundException(identifier);
        }
        return mapper.toResponse(product);
    }

    @Transactional
    public ProductResponse save(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = mapper.toEntity(request);
        product.setCategory(category);
        product.setProductIdentifier(UUID.randomUUID().toString());
        
        Product savedProduct = productRepository.save(product);
        return mapper.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse update(String identifier, ProductRequest request) {
        Product existingProduct = productRepository.findByProductIdentifier(identifier);
        if (existingProduct == null) {
            throw new ProductNotFoundException(identifier);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        updateProductFields(existingProduct, request, category);
        
        Product updatedProduct = productRepository.save(existingProduct);
        return mapper.toResponse(updatedProduct);
    }

    private void updateProductFields(Product product, ProductRequest request, Category category) {
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setCategory(category);
    }

    @Transactional
    public void delete(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
    }
}
