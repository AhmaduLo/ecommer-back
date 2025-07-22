package com.example.coindecoback.dto;


import lombok.Data;

import java.util.List;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;

    //  Champs SEO
    private String seoTitle;
    private String shortDescription;
    private String slug;

    //  URLs des images
    private List<String> imageUrls;
}

