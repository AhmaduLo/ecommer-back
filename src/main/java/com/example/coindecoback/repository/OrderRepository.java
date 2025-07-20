package com.example.coindecoback.repository;


import com.example.coindecoback.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Ex : retrouver les commandes par email client
    List<Order> findByUserEmail(String userEmail);
}
