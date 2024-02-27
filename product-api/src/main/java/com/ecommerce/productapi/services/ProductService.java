package com.ecommerce.productapi.services;

import com.ecommerce.productapi.domain.dto.ProductDto;
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
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllPageProducts(PageRequest page) {
        Page<Product> products = productRepository.findAll(page);
        return products.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductByCategoryId(Long categoryId) {
        List<Product> products = productRepository.getProductByCategory(categoryId);
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto findByProductIdentifier(String identifier) {
        Product product = productRepository.findByProductIdentifier(identifier);

        if (product != null) {
            return mapper.mapTo(product);
        }
        throw new ProductNotFoundException();
    }

    @Transactional
    public ProductDto save(ProductDto productDto) {
        productDto.setProductIdentifier(productDto.getProductIdentifier().toLowerCase());
        productDto.setName(productDto.getName().toLowerCase());

        Product existingProduct = productRepository.findByProductIdentifier(productDto.getProductIdentifier());
        if (existingProduct != null) throw new ProductAlreadyExistsException();

        boolean existsCategory = categoryRepository.existsById(productDto.getCategory().getId());
        if (!existsCategory) throw new CategoryNotFoundException();

        Product product = productRepository.save(mapper.mapFrom(productDto));
        return mapper.mapTo(product);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto, String identifier) {
        Product existingProduct = productRepository.findByProductIdentifier(identifier.toLowerCase());
        if (existingProduct == null) throw new ProductNotFoundException();

        boolean existsCategory = categoryRepository.existsById(productDto.getCategory().getId());
        if (!existsCategory) throw new CategoryNotFoundException();

        Category category = categoryMapper.mapFrom(productDto.getCategory());

        existingProduct.setProductIdentifier(identifier.toLowerCase());
        existingProduct.setName(Objects.requireNonNullElse(productDto.getName(), existingProduct.getName().toLowerCase()));
        existingProduct.setDescription(Objects.requireNonNullElse(productDto.getDescription(), existingProduct.getDescription()));
        existingProduct.setPrice(Objects.requireNonNullElse(productDto.getPrice(), existingProduct.getPrice()));
        existingProduct.setCategory(category);

        Product updateProduct = productRepository.save(existingProduct);
        return mapper.mapTo(updateProduct);
    }

    @Transactional
    public void delete(Long productId) throws ProductNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
