package com.ecommerce.shoppingapi.repositories;

import com.ecommerce.shoppingapi.domain.entities.ShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<ShopEntity, Long>, ReportRepository {

     List<ShopEntity> findAllByUserIdentifier(String userIdentifier);

     List<ShopEntity> findAllByDateGreaterThan(LocalDateTime date);
}
