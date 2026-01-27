package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.UserNotificationPreferencesDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.entity.UserNotificationPreferences;
import com.hirehub.hirehub_backend.repository.UserNotificationPreferencesRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationPreferencesServiceImpl implements UserNotificationPreferencesService {
    
    @Autowired
    private UserNotificationPreferencesRepository preferencesRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserNotificationPreferencesDto getPreferences(Long userId) throws Exception {
        UserNotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElse(null);
        
        if (preferences == null) {
            // Create default preferences if they don't exist
            createDefaultPreferences(userId);
            preferences = preferencesRepository.findByUserId(userId)
                    .orElseThrow(() -> new Exception("Failed to create preferences"));
        }
        
        return toDto(preferences);
    }
    
    @Override
    public UserNotificationPreferencesDto updatePreferences(Long userId, UserNotificationPreferencesDto preferencesDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        UserNotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserNotificationPreferences newPrefs = new UserNotificationPreferences();
                    newPrefs.setUser(user);
                    return newPrefs;
                });
        
        // Update preferences
        if (preferencesDto.getEmailApplicationStatus() != null) {
            preferences.setEmailApplicationStatus(preferencesDto.getEmailApplicationStatus());
        }
        if (preferencesDto.getEmailNewJobs() != null) {
            preferences.setEmailNewJobs(preferencesDto.getEmailNewJobs());
        }
        if (preferencesDto.getEmailJobAlerts() != null) {
            preferences.setEmailJobAlerts(preferencesDto.getEmailJobAlerts());
        }
        if (preferencesDto.getEmailSystemUpdates() != null) {
            preferences.setEmailSystemUpdates(preferencesDto.getEmailSystemUpdates());
        }
        if (preferencesDto.getInAppApplicationStatus() != null) {
            preferences.setInAppApplicationStatus(preferencesDto.getInAppApplicationStatus());
        }
        if (preferencesDto.getInAppNewJobs() != null) {
            preferences.setInAppNewJobs(preferencesDto.getInAppNewJobs());
        }
        if (preferencesDto.getInAppJobAlerts() != null) {
            preferences.setInAppJobAlerts(preferencesDto.getInAppJobAlerts());
        }
        if (preferencesDto.getInAppSystemUpdates() != null) {
            preferences.setInAppSystemUpdates(preferencesDto.getInAppSystemUpdates());
        }
        
        UserNotificationPreferences savedPreferences = preferencesRepository.save(preferences);
        return toDto(savedPreferences);
    }
    
    @Override
    public UserNotificationPreferencesDto createDefaultPreferences(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        UserNotificationPreferences preferences = new UserNotificationPreferences();
        preferences.setUser(user);
        // Default values are set in constructor
        
        UserNotificationPreferences savedPreferences = preferencesRepository.save(preferences);
        return toDto(savedPreferences);
    }
    
    @Override
    public boolean isEmailNotificationEnabled(Long userId, String notificationType) {
        UserNotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElse(new UserNotificationPreferences()); // Default: all enabled
        
        return switch (notificationType) {
            case "APPLICATION_STATUS" -> preferences.getEmailApplicationStatus() != null && preferences.getEmailApplicationStatus();
            case "NEW_JOB" -> preferences.getEmailNewJobs() != null && preferences.getEmailNewJobs();
            case "JOB_ALERT" -> preferences.getEmailJobAlerts() != null && preferences.getEmailJobAlerts();
            case "SYSTEM" -> preferences.getEmailSystemUpdates() != null && preferences.getEmailSystemUpdates();
            default -> true; // Default to enabled if type not recognized
        };
    }
    
    @Override
    public boolean isInAppNotificationEnabled(Long userId, String notificationType) {
        UserNotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElse(new UserNotificationPreferences()); // Default: all enabled
        
        return switch (notificationType) {
            case "APPLICATION_STATUS" -> preferences.getInAppApplicationStatus() != null && preferences.getInAppApplicationStatus();
            case "NEW_JOB" -> preferences.getInAppNewJobs() != null && preferences.getInAppNewJobs();
            case "JOB_ALERT" -> preferences.getInAppJobAlerts() != null && preferences.getInAppJobAlerts();
            case "SYSTEM" -> preferences.getInAppSystemUpdates() != null && preferences.getInAppSystemUpdates();
            default -> true; // Default to enabled if type not recognized
        };
    }
    
    // Helper method to convert entity to DTO
    private UserNotificationPreferencesDto toDto(UserNotificationPreferences preferences) {
        return new UserNotificationPreferencesDto(
                preferences.getId(),
                preferences.getUser() != null ? preferences.getUser().getId() : null,
                preferences.getEmailApplicationStatus(),
                preferences.getEmailNewJobs(),
                preferences.getEmailJobAlerts(),
                preferences.getEmailSystemUpdates(),
                preferences.getInAppApplicationStatus(),
                preferences.getInAppNewJobs(),
                preferences.getInAppJobAlerts(),
                preferences.getInAppSystemUpdates(),
                preferences.getCreatedAt(),
                preferences.getUpdatedAt()
        );
    }
}

