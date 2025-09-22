package com.hirehub.hirehub_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;


public class JwtProvider {
    // ✅ Use a strong secret key (minimum 32 characters)
    public static final SecretKey key =
            Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // ✅ Generate JWT Token
    public static String generateToken(Authentication auth, Long userId) {
        String email = auth.getName();
        String role = "";

        // Extract role from authorities (remove "ROLE_" prefix if present)
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (!authorities.isEmpty()) {
            role = authorities.iterator().next().getAuthority().replace("ROLE_", "");
        }

        System.out.println("Role being added to JWT: " + role);

        return Jwts.builder()
                .setIssuer("Mahima")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiry
                .claim("email", email)
                .claim("role", role)
                .claim("userId",userId)
                .signWith(key)
                .compact();
    }

    // ✅ Extract email from JWT token
    public static String getEmailFromToken(String token) {
        try {
            token = cleanToken(token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse JWT: " + e.getMessage(), e);
        }
    }

    // ✅ Extract role from JWT token
    public static String getRoleFromToken(String token) {
        token = cleanToken(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    public static Long getUserIdFromToken(String token) {
        token = cleanToken(token);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }


    // ✅ Helper: remove Bearer prefix + whitespace
    private static String cleanToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token.trim().replaceAll("\\s+", "");
    }
}



