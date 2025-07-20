package com.example.coindecoback.controller;


import com.example.coindecoback.dto.ProductDto;
import com.example.coindecoback.entity.Product;
import com.example.coindecoback.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Pour autoriser Angular à accéder à l'API
public class ProductController {

    @Autowired
    private ProductService productService;

    // Obtenir la liste de tous les produits
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Obtenir un seul produit par son ID
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id).orElseThrow();
    }

    // Ajouter un nouveau produit (admin)
    @PostMapping
    public Product createProduct(@RequestBody ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .imageUrl(productDto.getImageUrl())
                .stock(productDto.getStock())
                .build();
        return productService.saveProduct(product);
    }

    // Modifier un produit existant (admin)
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Product product = productService.getProductById(id).orElseThrow();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setStock(productDto.getStock());
        return productService.saveProduct(product);
    }

    // Supprimer un produit (admin)
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}

