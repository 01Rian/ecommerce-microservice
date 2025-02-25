package com.ecommerce.shoppingapi.repositories.impl;

import com.ecommerce.shoppingapi.domain.dto.report.ShopReportResponseDto;
import com.ecommerce.shoppingapi.domain.entities.Shop;
import com.ecommerce.shoppingapi.repositories.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportRepositoryImpl implements ReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Shop> getShopByFilters(LocalDate startDate, LocalDate endDate, BigDecimal maxValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT s ");
        sb.append("FROM Shop s ");
        sb.append("WHERE s.date >= :startDate ");

        if (endDate != null) {
            sb.append("AND s.date <= :endDate ");
        }

        if (maxValue != null) {
            sb.append("AND s.total <= :maxValue ");
        }

        TypedQuery<Shop> query = entityManager.createQuery(sb.toString(), Shop.class);
        query.setParameter("startDate", startDate.atTime(0, 0));

        if (endDate != null) {
            query.setParameter("endDate", endDate.atTime(23, 59));
        }

        if (maxValue != null) {
            query.setParameter("maxValue", maxValue);
        }

        return query.getResultList();
    }

    @Override
    public ShopReportResponseDto getReportByDate(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(sp.id), sum(sp.total), avg(sp.total) ");
        sb.append("FROM shopping.shop sp ");
        sb.append("WHERE sp.date >= :startDate ");
        sb.append("AND sp.date <= :endDate ");

        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("startDate", startDate.atTime(0, 0));
        query.setParameter("endDate", endDate.atTime(23, 59));

        Object[] result = (Object[]) query.getSingleResult();
        return ShopReportResponseDto.builder()
            .count(((Long) result[0]).intValue())
            .total(result[1] != null ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO)
            .mean(result[2] != null ? new BigDecimal(result[2].toString()) : BigDecimal.ZERO)
            .build();
    }
}
