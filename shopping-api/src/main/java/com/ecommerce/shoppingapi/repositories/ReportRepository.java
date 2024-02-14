package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.domain.entities.ShopEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository {

    public List<ShopEntity> getShopByFilters(
            LocalDate startDate,
            LocalDate endDate,
            Float maxValue
    );

    public ShopReportDto getReportByDate(
            LocalDate startDate,
            LocalDate endDate
    );
}
