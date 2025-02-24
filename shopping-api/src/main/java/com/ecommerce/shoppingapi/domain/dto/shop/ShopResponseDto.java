package com.ecommerce.shoppingapi.domain.dto.shop;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ShopResponseDto extends ShopDto {
    // Herda todos os campos do ShopDto
}