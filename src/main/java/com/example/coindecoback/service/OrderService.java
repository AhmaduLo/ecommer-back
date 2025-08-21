package com.example.coindecoback.service;

import com.example.coindecoback.entity.Order;
import com.example.coindecoback.entity.OrderItem;
import com.example.coindecoback.entity.OrderStatus;
import com.example.coindecoback.repository.OrderRepository;
import com.example.coindecoback.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;
    private List<Long> productIds;

    // Enregistrer une nouvelle commande
    public Order createOrder(Order order) {
        order.setCreatedAt(LocalDateTime.now());

        // Vérifie si un panier EN_ATTENTE existe pour ce client
        List<Order> existingOrders = orderRepository.findByFullNameAndUserEmailAndAddressAndStatus(
                order.getFullName(),
                order.getUserEmail(),
                order.getAddress(),
                OrderStatus.EN_ATTENTE
        );

        if (!existingOrders.isEmpty()) {
            Order panier = existingOrders.get(0);

            // Fusionne les quantités pour les produits existants
            for (OrderItem newItem : order.getItems()) {
                boolean found = false;
                for (OrderItem existingItem : panier.getItems()) {
                    if (existingItem.getProductId().equals(newItem.getProductId())) {
                        existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    panier.getItems().add(newItem);
                }
            }

            double total = calculateTotal(panier.getItems());
            panier.setTotalPrice(total);
            return orderRepository.save(panier);
        }

        // Nouveau panier
        order.setAccessToken(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.EN_ATTENTE);
        double total = calculateTotal(order.getItems());
        order.setTotalPrice(total);
        return orderRepository.save(order);
    }

    // Méthode utilitaire pour le total
    private double calculateTotal(List<OrderItem> items) {
        return items.stream()
                .mapToDouble(item -> {
                    var product = productRepository.findById(item.getProductId()).orElseThrow();
                    return product.getPrice() * item.getQuantity();
                }).sum();
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

    public String removeProductFromOrder(Long orderId, Long productId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Commande introuvable."));

        // Vérifie que l'utilisateur est bien le propriétaire
        if (!order.getUserEmail().equals(email)) {
            throw new SecurityException("Vous n’avez pas le droit de modifier cette commande.");
        }

        // Vérifie que la commande est encore modifiable
        if (order.getStatus() != OrderStatus.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de modifier une commande déjà en cours ou validée.");
        }

        // Retire le produit de la liste s'il existe
        boolean removed = order.getProductIds().remove(productId);
        if (!removed) {
            throw new IllegalArgumentException("Le produit n’existe pas dans cette commande.");
        }

        // Si la liste devient vide, on supprime la commande
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            orderRepository.delete(order);
            return "Commande supprimée car aucun produit restant.";
        }

        // Recalculer le prix total
        double total = productRepository.findAllById(order.getProductIds())
                .stream()
                .mapToDouble(product -> product.getPrice())
                .sum();
        order.setTotalPrice(total);

        orderRepository.save(order);

        return "Produit supprimé de la commande.";
    }

    // ✅ Méthode de suivi public
    public Optional<Order> findByAccessToken(String token) {
        return orderRepository.findByAccessToken(token);
    }



}
