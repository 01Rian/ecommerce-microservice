package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.entities.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long>, ReportRepository {
     List<Shop> findAllByUserIdentifier(String userIdentifier);
}
