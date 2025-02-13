package com.ecommerce.productapi.controllers;

import com.ecommerce.productapi.domain.dto.request.ProductRequest;
import com.ecommerce.productapi.domain.dto.response.ProductResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final PagedResourcesAssembler<ProductResponse> assembler;

    @GetMapping
    public ResponseEntity<List<EntityModel<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        List<EntityModel<ProductResponse>> productModels = products.stream()
                .map(this::createProductEntityModel)
                .toList();
        return ResponseEntity.ok(productModels);
    }

    @GetMapping("/pageable")
    public ResponseEntity<PagedModel<EntityModel<ProductResponse>>> getAllPageProducts(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<ProductResponse> productPage = productService.getAllPageProducts(pageRequest);
        PagedModel<EntityModel<ProductResponse>> pagedModel = assembler.toModel(productPage, this::createProductEntityModel);
        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<EntityModel<ProductResponse>>> getProductByCategory(@PathVariable("id") Long categoryId) {
        List<ProductResponse> products = productService.getProductByCategoryId(categoryId);
        List<EntityModel<ProductResponse>> productModels = products.stream()
                .map(this::createProductEntityModel)
                .toList();
        return ResponseEntity.ok(productModels);
    }

    @GetMapping("/{identifier}")
    public ResponseEntity<EntityModel<ProductResponse>> getProductByIdentifier(@PathVariable("identifier") String identifier) {
        ProductResponse product = productService.findByProductIdentifier(identifier);
        return ResponseEntity.ok(createProductEntityModel(product));
    }

    @PostMapping
    public ResponseEntity<EntityModel<ProductResponse>> newProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse newProduct = productService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createProductEntityModel(newProduct));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<EntityModel<ProductResponse>> updateProduct(
            @Valid @RequestBody ProductRequest request,
            @PathVariable("identifier") String identifier) {
        ProductResponse updatedProduct = productService.update(identifier, request);
        return ResponseEntity.ok(createProductEntityModel(updatedProduct));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ProductResponse> createProductEntityModel(ProductResponse product) {
        EntityModel<ProductResponse> entityModel = EntityModel.of(product);

        WebMvcLinkBuilder linkToProducts = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getAllProducts());
        entityModel.add(linkToProducts.withRel("all-products"));

        WebMvcLinkBuilder linkToSelf = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getProductByIdentifier(product.getProductIdentifier()));
        entityModel.add(linkToSelf.withRel("self"));

        return entityModel;
    }
}
