package com.example.coindecoback.controller;


import com.example.coindecoback.entity.Order;
import com.example.coindecoback.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.coindecoback.dto.OrderDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Passer une nouvelle commande
    @PostMapping
    public Order createOrder(@RequestBody OrderDto dto) {
        Order order = Order.builder()
                .fullName(dto.getFullName())
                .userEmail(dto.getUserEmail())
                .address(dto.getAddress())
                .productIds(dto.getProductIds())
                .status("en attente")
                .createdAt(LocalDateTime.now())
                .build();
        return orderService.createOrder(order);
    }

    // Obtenir toutes les commandes (admin)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Obtenir les commandes d’un client
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    public List<Order> getOrdersByEmail(@RequestParam String email) {
        return orderService.getOrdersByEmail(email);
    }

    // Modifier le statut d'une commande (admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }
}

