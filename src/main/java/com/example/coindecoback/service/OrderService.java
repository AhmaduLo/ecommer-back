package com.example.coindecoback.service;

import com.example.coindecoback.entity.Order;
import com.example.coindecoback.entity.OrderStatus;
import com.example.coindecoback.repository.OrderRepository;
import com.example.coindecoback.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // Enregistrer une nouvelle commande
    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.EN_COURS);// statut par défaut

        double total = productRepository.findAllById(order.getProductIds())
                .stream()
                .mapToDouble(product -> product.getPrice())
                .sum();

        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    // Récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Récupérer les commandes d’un utilisateur par email
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByUserEmail(email);
    }

    // Mettre à jour le statut d’une commande
    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id).orElseThrow();
        OrderStatus status = OrderStatus.valueOf(newStatus.toUpperCase());
        OrderStatus currentStatus = order.getStatus();
        OrderStatus requestedStatus = OrderStatus.valueOf(newStatus.toUpperCase());


        // 🚫 Bloquer les régressions
        if (currentStatus == OrderStatus.VALIDÉ && requestedStatus != OrderStatus.VALIDÉ) {
            throw new IllegalStateException("Impossible de modifier une commande déjà validée.");
        }

        if (currentStatus == OrderStatus.EN_ATTENTE && requestedStatus == OrderStatus.EN_COURS) {
            throw new IllegalStateException("Impossible de revenir à EN_COURS après traitement.");
        }

        // ✅ Ajouter la date si la commande est validée
        if (requestedStatus == OrderStatus.VALIDÉ && order.getValidatedAt() == null) {
            order.setValidatedAt(LocalDateTime.now());
        }

        order.setStatus(status);
        return orderRepository.save(order);


    }


}
