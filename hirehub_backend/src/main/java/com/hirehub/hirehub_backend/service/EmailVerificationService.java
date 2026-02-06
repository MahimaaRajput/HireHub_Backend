package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

public interface EmailVerificationService {
    void sendVerificationEmail(User user) throws Exception;
    boolean verifyEmail(String token) throws Exception;
    void resendVerificationEmail(String email) throws Exception;
    String generateVerificationToken();
}


