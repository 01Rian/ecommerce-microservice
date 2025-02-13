package com.ecommerce.productapi.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    
    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;
    
    @NotBlank(message = "A descrição da categoria é obrigatória")
    private String description;
} 