package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

public interface PhoneVerificationService {
    void sendVerificationOtp(User user) throws Exception;
    boolean verifyPhone(String phoneNumber, String otp) throws Exception;
    void resendVerificationOtp(String phoneNumber) throws Exception;
    String generateOtp();
}

