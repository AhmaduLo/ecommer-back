package com.example.coindecoback.controller;


import com.example.coindecoback.entity.Order;
import com.example.coindecoback.entity.OrderStatus;
import com.example.coindecoback.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.coindecoback.dto.OrderDto;
import org.springframework.web.server.ResponseStatusException;

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
                .status(OrderStatus.EN_ATTENTE)
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
    @GetMapping("/user")
    public List<Order> getOrdersByEmail(@RequestParam String email) {
        return orderService.getOrdersByEmail(email);
    }

    // ✅ Suivi public d'une commande via accessToken
    @GetMapping("/track")
    public Order getOrderByAccessToken(@RequestParam String token) {
        return orderService.findByAccessToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Commande introuvable"));
    }

    // Modifier le statut d'une commande (admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    @PostMapping("/confirm")
    public Order confirmAndCreateOrder(@RequestBody OrderDto dto) {
        // 1. Vérifie le paiement Stripe
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(dto.getPaymentIntentId());

            if (!"succeeded".equals(paymentIntent.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le paiement n’a pas été confirmé.");
            }

            // 2. Crée la commande si paiement OK
            Order order = Order.builder()
                    .fullName(dto.getFullName())
                    .userEmail(dto.getUserEmail())
                    .address(dto.getAddress())
                    .productIds(dto.getProductIds())
                    .totalPrice(dto.getTotalPrice())
                    .paymentIntentId(dto.getPaymentIntentId())
                    .status(OrderStatus.EN_COURS) // ici on passe en cours
                    .createdAt(LocalDateTime.now())
                    .build();

            return orderService.createOrder(order);

        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur Stripe : " + e.getMessage());
        }
    }

}

