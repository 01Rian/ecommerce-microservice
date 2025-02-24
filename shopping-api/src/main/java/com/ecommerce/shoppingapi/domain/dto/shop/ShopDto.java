package com.ecommerce.shoppingapi.domain.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ShopDto {

    private Long id;
    private String userIdentifier;
    private BigDecimal total;
    private LocalDateTime date;
    private List<ItemDto> items;
}
