package com.example.coindecoback.service;

import com.example.coindecoback.entity.Product;
import com.example.coindecoback.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Récupérer tous les produits
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Récupérer un produit par ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Ajouter ou modifier un produit
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    // Supprimer un produit par ID
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
