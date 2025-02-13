package com.ecommerce.productapi.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    
    @NotBlank(message = "O nome do produto é obrigatório")
    private String name;
    
    @NotBlank(message = "A descrição do produto é obrigatória")
    private String description;
    
    @NotNull(message = "O preço do produto é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal price;
    
    @NotNull(message = "A quantidade do produto é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    private Integer quantity;
    
    @NotNull(message = "O ID da categoria é obrigatório")
    private Long categoryId;
} 