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

    @GetMapping("/product")
    public List<ProductDto> getProducts() {
        return productService.getAll();
    }

    @GetMapping("/product/category/{id}")
    public List<ProductDto> getProductByCategory(@PathVariable("id") long categoryId) {
        return productService.getProductByCategoryId(categoryId);
    }

    @GetMapping("/product/{identifier}")
    public ProductDto findById(@PathVariable("identifier") String productIdentifier) {
        return productService.findByProductIdentifier(productIdentifier);
    }

    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto newProduct(@Valid @RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @DeleteMapping("/product/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        productService.delete(id);
    }
}
