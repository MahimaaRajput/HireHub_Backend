package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryVerifyDto {
    private String identifier; // email or phone number
    private String code;
    private String type; // "email" or "phone"
}



