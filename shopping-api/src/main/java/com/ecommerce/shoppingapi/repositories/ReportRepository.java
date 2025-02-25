package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {

     List<Shop> getShopByFilters(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal maxValue
    );

     ShopReportResponseDto getReportByDate(
            LocalDate startDate,
            LocalDate endDate
    );
}
