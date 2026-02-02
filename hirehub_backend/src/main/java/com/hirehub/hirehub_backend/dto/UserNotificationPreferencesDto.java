package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationPreferencesDto {
    private Long id;
    private Long userId;
    private Boolean emailApplicationStatus;
    private Boolean emailNewJobs;
    private Boolean emailJobAlerts;
    private Boolean emailSystemUpdates;
    private Boolean inAppApplicationStatus;
    private Boolean inAppNewJobs;
    private Boolean inAppJobAlerts;
    private Boolean inAppSystemUpdates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


