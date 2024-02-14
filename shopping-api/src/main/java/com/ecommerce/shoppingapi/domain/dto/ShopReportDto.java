package com.ecommerce.shoppingapi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopReportDto {

    private Integer count;
    private Double total;
    private Double mean;
}
