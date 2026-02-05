package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.UserNotificationPreferencesDto;

public interface UserNotificationPreferencesService {
    UserNotificationPreferencesDto getPreferences(Long userId) throws Exception;
    UserNotificationPreferencesDto updatePreferences(Long userId, UserNotificationPreferencesDto preferencesDto) throws Exception;
    UserNotificationPreferencesDto createDefaultPreferences(Long userId) throws Exception;
    boolean isEmailNotificationEnabled(Long userId, String notificationType);
    boolean isInAppNotificationEnabled(Long userId, String notificationType);
}





