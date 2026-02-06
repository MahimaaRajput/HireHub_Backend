package com.hirehub.hirehub_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Value("${captcha.secret-key:}")
    private String captchaSecretKey;

    @Value("${captcha.enabled:false}")
    private boolean captchaEnabled;

    @Value("${captcha.verify-url:https://www.google.com/recaptcha/api/siteverify}")
    private String captchaVerifyUrl;

    private final RestTemplate restTemplate;

    public CaptchaServiceImpl() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean verifyCaptcha(String captchaToken) throws Exception {
        if (!captchaEnabled) {
            // If CAPTCHA is disabled, always return true (for development)
            return true;
        }

        if (captchaToken == null || captchaToken.isEmpty()) {
            throw new Exception("CAPTCHA token is required");
        }

        if (captchaSecretKey == null || captchaSecretKey.isEmpty()) {
            throw new Exception("CAPTCHA secret key is not configured");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("secret", captchaSecretKey);
            params.add("response", captchaToken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            @SuppressWarnings("unchecked")
            ResponseEntity<Map> response = restTemplate.postForEntity(captchaVerifyUrl, request, Map.class);

            if (response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Boolean success = (Boolean) responseBody.get("success");
                return success != null && success;
            }

            return false;
        } catch (Exception e) {
            System.err.println("CAPTCHA verification error: " + e.getMessage());
            throw new Exception("Failed to verify CAPTCHA: " + e.getMessage());
        }
    }

    @Override
    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }
}

