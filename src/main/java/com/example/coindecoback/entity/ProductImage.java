package com.example.coindecoback.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore // ignore la boucle vers Product lors de la sérialisation
    private Product product;


}

