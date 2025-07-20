package com.example.coindecoback.controller;


import com.example.coindecoback.dto.AdminLoginDto;
import com.example.coindecoback.entity.AdminUser;
import com.example.coindecoback.jwt.JwtUtils;
import com.example.coindecoback.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*") // Permet les appels du front Angular
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtils jwtUtils;


    // ✅ Connexion d'un administrateur
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AdminLoginDto dto) {
        Optional<AdminUser> adminOpt = adminService.authenticate(dto.getEmail(), dto.getPassword());
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            // 🔐 Génére le token avec email et rôle
            String token = jwtUtils.generateToken(admin.getEmail(), admin.getRole());

            return Map.of("message", "Connexion réussie", "token", token);
        } else {
            return Map.of("message", "Email ou mot de passe invalide");
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

