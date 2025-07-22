package com.example.coindecoback.mapper;

import com.example.coindecoback.dto.ProductDto;
import com.example.coindecoback.entity.Product;
import com.example.coindecoback.entity.ProductImage;

public class ProductMapper {

    // Convertit un Product en ProductDto
    public static ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory());
        dto.setSeoTitle(product.getSeoTitle());
        dto.setShortDescription(product.getShortDescription());
        dto.setSlug(product.getSlug());

        // Images
        if (product.getImages() != null) {
            dto.setImageUrls(product.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .toList());
        }

        return dto;
    }

    // Convertit un ProductDto en Product
    public static Product toEntity(ProductDto dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setSeoTitle(dto.getSeoTitle());
        product.setShortDescription(dto.getShortDescription());
        product.setSlug(dto.getSlug());

        // ⚠️ Les images seront ajoutées dans le service si nécessaire
        return product;
    }
}

