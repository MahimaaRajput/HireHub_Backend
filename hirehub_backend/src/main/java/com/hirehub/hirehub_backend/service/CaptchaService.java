package com.hirehub.hirehub_backend.service;

public interface CaptchaService {
    boolean verifyCaptcha(String captchaToken) throws Exception;
    boolean isCaptchaEnabled();
}



