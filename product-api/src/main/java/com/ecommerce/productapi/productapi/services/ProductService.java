package com.ecommerce.productapi.productapi.services;

import com.ecommerce.productapi.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.productapi.domain.entities.ProductEntity;
import com.ecommerce.productapi.productapi.exception.CategoryNotFoundException;
import com.ecommerce.productapi.productapi.exception.ProductAlreadyExistsException;
import com.ecommerce.productapi.productapi.exception.ProductNotFoundException;
import com.ecommerce.productapi.productapi.mappers.impl.ProductMapper;
import com.ecommerce.productapi.productapi.repositories.CategoryRepository;
import com.ecommerce.productapi.productapi.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper mapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, ProductMapper mapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllPageProducts(PageRequest page) {
        Page<ProductEntity> products = productRepository.findAll(page);
        return products.map(mapper::mapTo);
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductByCategoryId(Long categoryId) {
        List<ProductEntity> products = productRepository.getProductByCategory(categoryId);
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto findByProductIdentifier(String identifier) {
        ProductEntity product = productRepository.findByProductIdentifier(identifier);

        if (product != null) {
            return mapper.mapTo(product);
        }
        throw new ProductNotFoundException();
    }

    @Transactional
    public ProductDto save(ProductDto productDto) {
        productDto.setProductIdentifier(productDto.getProductIdentifier().toLowerCase());
        productDto.setNome(productDto.getNome().toLowerCase());

        ProductEntity existingProduct = productRepository.findByProductIdentifier(productDto.getProductIdentifier());
        if (existingProduct != null) throw new ProductAlreadyExistsException();

        boolean existsCategory = categoryRepository.existsById(productDto.getCategory().getId());
        if (!existsCategory) throw new CategoryNotFoundException();

        ProductEntity product = productRepository.save(mapper.mapFrom(productDto));
        return mapper.mapTo(product);
    }

    @Transactional
    public void delete(Long productId) throws ProductNotFoundException {
        ProductEntity product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
