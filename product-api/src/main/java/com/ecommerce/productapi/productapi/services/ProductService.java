package com.ecommerce.productapi.productapi.services;

import com.ecommerce.productapi.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.productapi.domain.entities.ProductEntity;
import com.ecommerce.productapi.productapi.mappers.impl.ProductMapper;
import com.ecommerce.productapi.productapi.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper mapper;

    public List<ProductDto> getAll() {
        List<ProductEntity> products = productRepository.findAll();
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public List<ProductDto> getProductByCategoryId(Long categoryId) {
        List<ProductEntity> products = productRepository.getProductByCategory(categoryId);
        return products
                .stream()
                .map(mapper::mapTo)
                .collect(Collectors.toList());
    }

    public ProductDto findByProductIdentifier(String identifier) {
        ProductEntity product = productRepository.findByProductIdentifier(identifier);
        if (product != null) {
            return mapper.mapTo(product);
        }
        return null;
    }

    @Transactional
    public ProductDto save(ProductDto productDto) {
        productDto.setProductIdentifier(productDto.getProductIdentifier().toLowerCase());
        productDto.setNome(productDto.getNome().toLowerCase());

        ProductEntity existingProduct = productRepository.findByProductIdentifier(productDto.getProductIdentifier());

        if (existingProduct != null) {
            return null;
        } else {
            ProductEntity product = productRepository.save(mapper.mapFrom(productDto));
            return mapper.mapTo(product);
        }
    }

    public void delete(long productId) {
        Optional<ProductEntity> product = productRepository.findById(productId);
        product.ifPresent(productEntity -> productRepository.delete(productEntity));
    }
}
