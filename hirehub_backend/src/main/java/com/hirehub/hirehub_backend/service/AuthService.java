package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.AuthResponse;
import com.hirehub.hirehub_backend.dto.UserLoginRequest;
import com.hirehub.hirehub_backend.dto.UserRegisterRequest;
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
    @Autowired
    private ProfileService profileService;
    @Autowired
    private EmailVerificationService emailVerificationService;
    @Autowired
    private TwoFactorAuthService twoFactorAuthService;
    @Autowired
    private CaptchaService captchaService;

    public AuthResponse register(UserRegisterRequest reqUser) throws Exception {
        // Verify CAPTCHA if enabled
        if (captchaService.isCaptchaEnabled()) {
            if (reqUser.getCaptchaToken() == null || reqUser.getCaptchaToken().isEmpty()) {
                throw new Exception("CAPTCHA verification is required");
            }
            boolean captchaValid = captchaService.verifyCaptcha(reqUser.getCaptchaToken());
            if (!captchaValid) {
                throw new Exception("CAPTCHA verification failed. Please try again.");
            }
        }

        Optional<User> founduser = userRepository.findByEmail(reqUser.getEmail());
        if (founduser.isPresent()) {
            throw new Exception("user already registered with this email: " + reqUser.getEmail());
        }
        User user= reqUser.toEntity();
        reqUser.setProfileId(profileService.createProfile(reqUser.getEmail()));
        user.setPassword(passwordEncoder.encode(reqUser.getPassword()));
        User savedUser = userRepository.save(user);
        
        // Send email verification
        try {
            emailVerificationService.sendVerificationEmail(savedUser);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
        String token = JwtProvider.generateToken(authentication, savedUser.getId());
        return AuthResponse.builder()
                .token(token)
                .message("Registration successful! Please check your email to verify your account.")
                .build();
    }


        public AuthResponse login(UserLoginRequest request) {

            //  Check if user exists with given email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email not registered"));

            //  Check password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid password");
            }

            // Check if 2FA is enabled
            if (twoFactorAuthService.is2FAEnabled(user)) {
                // Return response indicating 2FA is required
                return AuthResponse.builder()
                        .token(null)
                        .message("2FA verification required. Please provide the 2FA code.")
                        .requires2FA(true)
                        .build();
            }

            // Create Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(user.getRole().name()))
            );

            //  Generate JWT token
            String token = JwtProvider.generateToken(authentication,user.getId());

            //  Return token + user data
            return AuthResponse.builder()
                    .token(token)
                    .message("Login success")
                    .requires2FA(false)
                    .build();
        }

        public AuthResponse verify2FALogin(String email, int code) {
            //  Check if user exists with given email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Email not registered"));

            // Check if 2FA is enabled
            if (!twoFactorAuthService.is2FAEnabled(user)) {
                throw new RuntimeException("2FA is not enabled for this account");
            }

            // Verify 2FA code
            if (user.getTwoFactorSecret() == null) {
                throw new RuntimeException("2FA secret not found");
            }

            boolean isValid = twoFactorAuthService.verifyTotpCode(user.getTwoFactorSecret(), code);
            if (!isValid) {
                throw new RuntimeException("Invalid 2FA code");
            }

            // Create Authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(user.getRole().name()))
            );

            //  Generate JWT token
            String token = JwtProvider.generateToken(authentication, user.getId());

            //  Return token + user data
            return AuthResponse.builder()
                    .token(token)
                    .message("Login success")
                    .requires2FA(false)
                    .build();
        }



}
