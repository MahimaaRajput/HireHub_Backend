package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TwoFactorSetupDto {
    private String secretKey;
    private String qrCodeUrl;
    private String message;
}

