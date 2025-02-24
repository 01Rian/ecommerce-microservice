package com.ecommerce.shoppingapi.domain.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopReportDto {

    private Integer count;
    private BigDecimal total;
    private BigDecimal mean;
}
