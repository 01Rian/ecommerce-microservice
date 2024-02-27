package com.ecommerce.productapi.repositories;

import com.ecommerce.productapi.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT p FROM Product p JOIN p.category c WHERE c.id = :categoryId")
    List<Product> getProductByCategory(@Param("categoryId") Long categoryId);

    Product findByProductIdentifier(String productIdentifier);
}
