package com.example.coindecoback.controller;

import com.example.coindecoback.dto.PaymentRequestDto;
import com.example.coindecoback.entity.Order;
import com.example.coindecoback.entity.OrderStatus;
import com.example.coindecoback.repository.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody PaymentRequestDto dto) {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(Long.valueOf(dto.getAmount())) // ⚠️ En centimes (ex : 3499 = 34,99 €)
                .setCurrency("eur")
                .setDescription("Commande via accessToken: " + dto.getAccessToken())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);

            // Récupérer la commande associée à l'accessToken
            Optional<Order> optionalOrder = orderRepository.findByAccessToken(dto.getAccessToken());

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                // Mettre à jour la commande : statut + paymentIntentId
                order.setStatus(OrderStatus.EN_COURS);
                order.setPaymentIntentId(intent.getId());

                orderRepository.save(order); // sauvegarder en base
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Commande non trouvée pour cet accessToken"
                ));
            }

            // Retourner les informations utiles
            return ResponseEntity.ok(Map.of(
                    "accessToken", dto.getAccessToken(),
                    "paymentIntentId", intent.getId(),
                    "clientSecret", intent.getClientSecret(),
                    "status", "EN_COUR"
            ));
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Erreur Stripe : " + e.getMessage()));
        }
    }
}

