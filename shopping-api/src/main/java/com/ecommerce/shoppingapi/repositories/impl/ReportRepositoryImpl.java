package com.ecommerce.shoppingapi.repositories.impl;

import com.ecommerce.shoppingapi.domain.dto.ShopReportDto;
import com.ecommerce.shoppingapi.domain.entities.ShopEntity;
import com.ecommerce.shoppingapi.repositories.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.time.LocalDate;
import java.util.List;

public class ReportRepositoryImpl implements ReportRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ShopEntity> getShopByFilters(LocalDate startDate, LocalDate endDate, Float maxValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT s ");
        sb.append("FROM ShopEntity s ");
        sb.append("WHERE s.date >= :startDate ");

        if (endDate != null) {
            sb.append("AND s.date <= :endDate ");
        }

        if (maxValue != null) {
            sb.append("AND s.total <= :maxValue ");
        }

        Query query = entityManager.createQuery(sb.toString());
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
    public ShopReportDto getReportByDate(LocalDate startDate, LocalDate endDate) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(sp.id), sum(sp.total), avg(sp.total) ");
        sb.append("FROM shopping.shop sp ");
        sb.append("WHERE sp.date >= :startDate ");
        sb.append("AND sp.date <= :endDate ");

        Query query = entityManager.createNativeQuery(sb.toString());
        query.setParameter("startDate", startDate.atTime(0, 0));
        query.setParameter("endDate", endDate.atTime(23, 59));

        Object[] result = (Object[]) query.getSingleResult();
        ShopReportDto shopReportDto = new ShopReportDto();
        shopReportDto.setCount(((Long) result[0]).intValue());
        shopReportDto.setTotal((Double) result[1]);
        shopReportDto.setMean((Double) result[2]);

        return shopReportDto;
    }
}
