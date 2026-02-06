package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.entity.User;

public interface AccountRecoveryService {
    void initiateRecoveryByEmail(String email) throws Exception;
    void initiateRecoveryByPhone(String phoneNumber) throws Exception;
    boolean verifyRecoveryCode(String identifier, String code, String type) throws Exception;
    User recoverAccount(String identifier, String code, String type) throws Exception;
    String generateRecoveryCode();
}

