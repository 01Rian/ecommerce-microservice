package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.ProductDto;
import com.ecommerce.productapi.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final PagedResourcesAssembler<ProductDto> assembler;

    @GetMapping
    public List<EntityModel<ProductDto>> getAllProducts() {
        List<ProductDto> productDtos = productService.getAllProducts();
        return productDtos.stream()
                .map(this::createProductEntityModel)
                .toList();
    }

    @GetMapping("/pageable")
    public PagedModel<EntityModel<ProductDto>> getAllPageProducts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<ProductDto> productPage = productService.getAllPageProducts(pageRequest);

        return assembler.toModel(productPage, this::createProductEntityModel);
    }

    @GetMapping("/category/{id}")
    public List<EntityModel<ProductDto>> getProductByCategory(@PathVariable("id") Long categoryId) {
        List<ProductDto> productDtos = productService.getProductByCategoryId(categoryId);
        return productDtos.stream()
                .map(this::createProductEntityModel)
                .toList();
    }

    @GetMapping("/{identifier}")
    public EntityModel<ProductDto> getProductByIdentifier(@PathVariable("identifier") String identifier) {
        ProductDto productDto = productService.findByProductIdentifier(identifier);
        return createProductEntityModel(productDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ProductDto> newProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto newProduct = productService.save(productDto);
        return createProductEntityModel(newProduct);
    }

    @PutMapping("/{identifier}")
    public EntityModel<ProductDto> updateProduct(@RequestBody ProductDto productDto, @PathVariable("identifier") String identifier) {
        ProductDto productDtoUpdated = productService.updateProduct(productDto, identifier);
        return createProductEntityModel(productDtoUpdated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        productService.delete(id);
    }

    private EntityModel<ProductDto> createProductEntityModel(ProductDto productDto) {
        EntityModel<ProductDto> entityModel = EntityModel.of(productDto);

        WebMvcLinkBuilder linkToProducts = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getAllProducts());
        entityModel.add(linkToProducts.withRel("all-products"));

        WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getProductByIdentifier(productDto.getProductIdentifier()));
        entityModel.add(linkToSelf.withRel("self"));

        return entityModel;
    }
}
