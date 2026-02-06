package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthServiceImpl implements TwoFactorAuthService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.name:HireHub}")
    private String appName;

    private GoogleAuthenticator gAuth;

    @Autowired
    public void init() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30000) // 30 seconds
                .setWindowSize(3) // Allow 3 time steps before/after
                .build();
        this.gAuth = new GoogleAuthenticator(config);
    }

    @Override
    public String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    @Override
    public String generateQrCodeUrl(User user, String secretKey) {
        String accountName = user.getEmail();
        String issuer = appName;
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secretKey).build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL(issuer, accountName, key);
    }

    @Override
    public boolean verifyTotpCode(String secretKey, int code) {
        return gAuth.authorize(secretKey, code);
    }

    @Override
    public void enable2FA(User user, String secretKey, int verificationCode) throws Exception {
        // Verify the code before enabling
        if (!verifyTotpCode(secretKey, verificationCode)) {
            throw new Exception("Invalid verification code. Please try again.");
        }

        user.setTwoFactorEnabled(true);
        user.setTwoFactorSecret(secretKey);
        userRepository.save(user);
    }

    @Override
    public void disable2FA(User user) throws Exception {
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
    }

    @Override
    public boolean is2FAEnabled(User user) {
        return user.getTwoFactorEnabled() != null && user.getTwoFactorEnabled();
    }
}

