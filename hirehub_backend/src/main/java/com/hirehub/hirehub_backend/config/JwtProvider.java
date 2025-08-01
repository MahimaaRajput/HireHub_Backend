package com.hirehub.hirehub_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;


public class JwtProvider {
    public static final  SecretKey key= Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    public static String generateToken(Authentication auth) {
        String email = auth.getName();
        String role = ""; // Default role blank

        // ✅ Extract 1st role from authorities
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (!authorities.isEmpty()) {
            role = authorities.iterator().next().getAuthority();  // e.g., ROLE_RECRUITER or ROLE_JOBSEEKER
        }

        return Jwts.builder()
                .setIssuer("Mahima")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .claim("email", email)
                .claim("role", role)
                .signWith(key)
                .compact();
    }
    public static String getEmailFromToken(String token)
    {
        token=token.substring(7); //remove "Bearer "
        Claims claims=Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email",String.class);

    }
    public static String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

}
