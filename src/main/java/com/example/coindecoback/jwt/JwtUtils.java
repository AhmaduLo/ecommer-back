package com.example.coindecoback.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // ✅ Clé d’au moins 64 caractères pour HS512
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final long jwtExpirationMs = 86400000; // 24h


    // 🔐 Génère un token JWT avec email et rôle
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // 📩 Récupère l'email (subject) depuis le token
    public String getEmailFromJwt(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 🔓 Récupère le rôle depuis le token
    public String getRoleFromJwt(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    // ✅ Vérifie la validité du token
    public boolean validateJwt(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("Token JWT invalide : " + e.getMessage());
            return false;
        }
    }

    // ⚙️ Méthode utilitaire pour parser le token (corrigée)
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}