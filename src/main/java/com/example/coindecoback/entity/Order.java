package com.example.coindecoback.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String userEmail;

    private String address;

    @ElementCollection
    private List<Long> productIds;

    private Double totalPrice;
    private String status; // ex: "en attente", "envoyé"

    private LocalDateTime createdAt;
}
