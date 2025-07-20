package com.example.coindecoback.dto;


import lombok.Data;

@Data
public class AdminLoginDto {
    private String email;
    private String password;
    private String role = "ADMIN";
}

