package com.hirehub.hirehub_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwtHeader = request.getHeader(JwtConstant.JWT_HEADER);

        if (jwtHeader != null && jwtHeader.startsWith("Bearer ")) {
            // Extract token safely
            String jwt = jwtHeader.substring(7).trim();
            jwt = jwt.replaceAll("\\s+", ""); // remove ALL whitespace


            try {
                // Log for debugging (remove in production)
                System.out.println("JWT Header: [" + jwtHeader + "]");
                System.out.println("Extracted Token: [" + jwt + "]");

                String email = JwtProvider.getEmailFromToken(jwt);
                String role = JwtProvider.getRoleFromToken(jwt);

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(() -> "ROLE_" + role);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                e.printStackTrace(); // log exact error
                throw new BadCredentialsException("Invalid Token: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
