package com.example.coindecoback.controller;


import com.example.coindecoback.dto.ProductDto;
import com.example.coindecoback.entity.Product;
import com.example.coindecoback.entity.ProductImage;
import com.example.coindecoback.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Product createProduct(@RequestBody ProductDto productDto) {
        Product product = Product.builder().name(productDto.getName()).description(productDto.getDescription()).price(productDto.getPrice()).category(productDto.getCategory()).stock(productDto.getStock()).seoTitle(productDto.getSeoTitle()).shortDescription(productDto.getShortDescription()).slug(productDto.getSlug()).build();

        // Ajout des images liées
        if (productDto.getImageUrls() != null) {
            List<ProductImage> images = productDto.getImageUrls().stream().map(url -> {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(product); // lien inverse
                return img;
            }).toList();
            product.setImages(images);
        }
        return productService.saveProduct(product);
    }

    // Modifier un produit existant (admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        Product product = productService.getProductById(id).orElseThrow();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());
        product.setStock(productDto.getStock());
        product.setSeoTitle(productDto.getSeoTitle());
        product.setShortDescription(productDto.getShortDescription());
        product.setSlug(productDto.getSlug());

        // 🖼️ Mise à jour des images
        if (productDto.getImageUrls() != null) {
            // Supprime les anciennes images liées (orphanRemoval = true dans l'entité)
            product.getImages().clear();

            // Crée et ajoute les nouvelles images
            List<ProductImage> newImages = productDto.getImageUrls().stream().map(url -> {
                ProductImage img = new ProductImage();
                img.setImageUrl(url);
                img.setProduct(product); // lien inverse
                return img;
            }).toList();

            product.getImages().addAll(newImages);
        }

        // 💾 Sauvegarde du produit avec les images mises à jour

        return productService.saveProduct(product);
    }

    // Supprimer un produit (admin)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category, @RequestParam(required = false) Double minPrice, @RequestParam(required = false) Double maxPrice, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<ProductDto> result = productService.searchProducts(keyword, category, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(result);
    }
}

