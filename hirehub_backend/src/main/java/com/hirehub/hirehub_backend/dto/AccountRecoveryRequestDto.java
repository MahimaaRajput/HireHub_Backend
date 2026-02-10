package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRecoveryRequestDto {
    private String identifier; // email or phone number
    private String type; // "email" or "phone"
}



