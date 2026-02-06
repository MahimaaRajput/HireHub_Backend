package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

public interface TwoFactorAuthService {
    String generateSecretKey();
    String generateQrCodeUrl(User user, String secretKey);
    boolean verifyTotpCode(String secretKey, int code);
    void enable2FA(User user, String secretKey, int verificationCode) throws Exception;
    void disable2FA(User user) throws Exception;
    boolean is2FAEnabled(User user);
}

