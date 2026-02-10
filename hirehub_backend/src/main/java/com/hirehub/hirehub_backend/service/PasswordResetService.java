package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

public interface PasswordResetService {
    void sendPasswordResetEmail(String email) throws Exception;
    boolean verifyResetToken(String token) throws Exception;
    void resetPassword(String token, String newPassword) throws Exception;
    String generateResetToken();
}



