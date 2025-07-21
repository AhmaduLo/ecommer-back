package com.example.coindecoback.jwt;

import com.example.coindecoback.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    // ✅ Récupère le token depuis le cookie "jwt"
    private String extractTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 🔐 Récupère le token JWT depuis le cookie
        String token = extractTokenFromCookies(request);

        if (token != null) {
            try {
                if (jwtUtils.validateJwt(token)) {
                    String email = jwtUtils.getEmailFromJwt(token);
                    Role role = Role.valueOf(jwtUtils.getRoleFromJwt(token)); // déjà Enum

                    // 🔐 Création de l'autorité Spring Security
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));

                    // ✅ Si pas déjà authentifié
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur de validation du token JWT : " + e.getMessage());
                // Tu peux aussi faire response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            }
        }

        // ▶️ Passe au filtre suivant
        filterChain.doFilter(request, response);
    }
}
