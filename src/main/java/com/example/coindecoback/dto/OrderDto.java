package com.example.coindecoback.dto;


import lombok.Data;

import java.util.List;

@Data
public class OrderDto {
    private String fullName;
    private String userEmail;
    private String address;
    private List<Long> productIds;
}

