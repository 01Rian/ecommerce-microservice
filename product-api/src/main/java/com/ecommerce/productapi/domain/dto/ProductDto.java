package com.ecommerce.productapi.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDto {

    private Long id;
    @NotBlank
    private String productIdentifier;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Float price;
    @NotNull
    private CategoryDto category;
}
