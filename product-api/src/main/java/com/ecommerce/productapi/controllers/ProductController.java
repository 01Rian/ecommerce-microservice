package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/pageable")
    public Page<ProductDto> getAllPageProducts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);

        return productService.getAllPageProducts(pageRequest);
    }

    @GetMapping("/category/{id}")
    public List<ProductDto> getProductByCategory(@PathVariable("id") Long categoryId) {
        return productService.getProductByCategoryId(categoryId);
    }

    @GetMapping("/{identifier}")
    public ProductDto getProductByIdentifier(@PathVariable("identifier") String identifier) {
        return productService.findByProductIdentifier(identifier);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto newProduct(@Valid @RequestBody ProductDto productDto) {
        return productService.save(productDto);
    }

    @PutMapping("/{identifier}")
    public ProductDto updateProduct(@RequestBody ProductDto productDto, @PathVariable("identifier") String identifier) {
        return productService.updateProduct(productDto, identifier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }
}
