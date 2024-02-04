package com.ecommerce.productapi.productapi.repositories;

import com.ecommerce.productapi.productapi.domain.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query(value = "SELECT p "
                    + "FROM product p "
                    + "JOIN category c ON p.category.id = c.id "
                    + "WHERE c.id = :categoryId ", nativeQuery=true)
    public List<ProductEntity> getProductByCategory(@Param("categoryId") long categoryId);

    public ProductEntity findByProductIdentifier(String productIdentifier);
}
