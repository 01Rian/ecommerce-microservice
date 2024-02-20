package com.ecommerce.productapi.productapi.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long    id;
    private String  nome;
    private Float   preco;
    private String  descricao;
    private String  productIdentifier;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}
