package com.example.coindecoback.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    // 🔁 Configure Stripe automatiquement au démarrage
    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        System.out.println("✅ Stripe API configurée");
    }
}
