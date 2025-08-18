package com.example.coindecoback.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String fullName;
    private String userEmail;
    private String address;
    private String phoneNumber;
    private String accessToken;
    private String status;
    private int amount; // en centimes
}
