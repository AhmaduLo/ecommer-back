package com.example.coindecoback.repository;



import com.example.coindecoback.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tu peux ajouter des méthodes personnalisées ici si besoin
}
