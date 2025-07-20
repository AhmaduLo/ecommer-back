package com.example.coindecoback.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {
    private String fullName;
    private String userEmail;
    private String address;
    private List<Long> productIds;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
}

