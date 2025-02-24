package com.ecommerce.shoppingapi.domain.dto.shop;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    
    private String productIdentifier;
    private BigDecimal price;
}
