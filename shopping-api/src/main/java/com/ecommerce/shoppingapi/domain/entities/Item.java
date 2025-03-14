package com.ecommerce.shoppingapi.domain.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
@Table(name = "item")
public class Item {

    private String productIdentifier;
    private BigDecimal price;
}
