package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.AuthResponse;
import com.hirehub.hirehub_backend.dto.UserRegisterRequest;
import com.hirehub.hirehub_backend.dto.UserResponse;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(UserRegisterRequest reqUser) throws Exception {
        Optional<User> founduser = userRepository.findByEmail(reqUser.getEmail());
        if (founduser.isPresent()) {
            throw new Exception("user already registered with this email: " + reqUser.getEmail());
        }
        User user = User.builder()
                .fullName(reqUser.getFullName())
                .email(reqUser.getEmail())
                .password(passwordEncoder.encode(reqUser.getPassword()))
                .role(reqUser.getRole())
                .gender(reqUser.getGender())
                .build();
        userRepository.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
        String token = JwtProvider.generateToken(authentication);
        return AuthResponse.builder()
                .token(token)
                .message("register success")
                .build();
    }

}
