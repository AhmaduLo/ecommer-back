package com.example.coindecoback.controller;


import com.example.coindecoback.dto.AdminLoginDto;
import com.example.coindecoback.entity.AdminUser;
import com.example.coindecoback.jwt.JwtUtils;
import com.example.coindecoback.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Permet les appels du front Angular
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtils jwtUtils;


    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final long BLOCK_DURATION_MS = TimeUnit.MINUTES.toMillis(15); // 15 minutes


    // ✅ Connexion d'un administrateur
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AdminLoginDto dto) {
        String email = dto.getEmail();

        // ⛔ Vérifie si le compte est bloqué
        if (blockedUntil.containsKey(email) && System.currentTimeMillis() < blockedUntil.get(email)) {
            long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(blockedUntil.get(email) - System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "Compte bloqué. Réessayez dans " + minutesLeft + " minutes.")).getBody();
        }

        Optional<AdminUser> adminOpt = adminService.authenticate(dto.getEmail(), dto.getPassword());

        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();

            // ✅ Connexion réussie → reset
            attempts.remove(email);
            blockedUntil.remove(email);

            // 🔐 Génére le token avec email et rôle
            String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole());

            // 🔐 Cookie HTTP-only
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(true) // ❗ Mets sur false en local, true en prod avec HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // 1 jour
                    .sameSite("Strict") // Ou "Lax" selon le frontend
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(Map.of("message", "Connexion réussie", "token", token)).getBody();

        } else {
            // ❌ Connexion échouée → incrémentation
            attempts.put(email, attempts.getOrDefault(email, 0) + 1);

            if (attempts.get(email) >= MAX_ATTEMPTS) {
                blockedUntil.put(email, System.currentTimeMillis() + BLOCK_DURATION_MS);
                attempts.remove(email);
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "Trop de tentatives. Compte bloqué pour 15 minutes.")).getBody();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Email ou mot de passe invalide")).getBody();

        }
    }

    // ✅ Création d’un nouvel administrateur (à désactiver en production)
    @PostMapping("/register")
    public Map<String, String> register(@RequestBody AdminLoginDto dto) {
        adminService.createAdmin(dto.getEmail(), dto.getPassword());
        return Map.of("message", "Admin créé avec succès");
    }

    // ✅ Suppression d’un administrateur par son ID
    @DeleteMapping("/{id}")
    public Map<String, String> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return Map.of("message", "Administrateur supprimé avec succès");
    }
}
