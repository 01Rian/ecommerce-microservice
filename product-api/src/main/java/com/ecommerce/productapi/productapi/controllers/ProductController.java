package com.ecommerce.productapi.productapi.controllers;

import com.ecommerce.productapi.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.productapi.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/products/category/{id}")
    public List<ProductDto> getProductByCategory(@PathVariable("id") Long categoryId) {
        return productService.getProductByCategoryId(categoryId);
    }

    @GetMapping("/products/{identifier}")
    public ProductDto getProductByIdentifier(@PathVariable("identifier") String identifier) {
        return productService.findByProductIdentifier(identifier);
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto newProduct(@Valid @RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }
}
