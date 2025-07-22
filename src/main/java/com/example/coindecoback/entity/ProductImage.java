package com.example.coindecoback.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    // 🔗 Produit associé
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}

