package com.example.coindecoback.entity;


public enum OrderStatus {
    EN_COURS,     // Client a passé la commande
    EN_ATTENTE,   // Plateforme attend la validation/fournisseur
    VALIDÉ        // Fournisseur a confirmé/expédié
}
