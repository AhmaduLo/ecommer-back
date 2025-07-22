package com.example.coindecoback.service;


import com.example.coindecoback.entity.AdminUser;
import com.example.coindecoback.entity.Role;
import com.example.coindecoback.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Créer un nouvel administrateur (avec mot de passe hashé)
    public AdminUser createAdmin(String email, String plainPassword) {
        AdminUser admin = AdminUser.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(plainPassword))
                .role(Role.valueOf("CLIENT"))
                .build();
        return adminUserRepository.save(admin);
    }

    // Vérifie les identifiants pour le login
    public Optional<AdminUser> authenticate(String email, String plainPassword) {
        Optional<AdminUser> adminOpt = adminUserRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            AdminUser admin = adminOpt.get();
            if (passwordEncoder.matches(plainPassword, admin.getPasswordHash())) {
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }

    public void deleteAdmin(Long id) {
        adminUserRepository.deleteById(id);
    }
}
