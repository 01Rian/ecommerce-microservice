package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {

     List<Shop> getShopByFilters(
            LocalDate startDate,
            LocalDate endDate,
            Float maxValue
    );

     ShopReportDto getReportByDate(
            LocalDate startDate,
            LocalDate endDate
    );
}
