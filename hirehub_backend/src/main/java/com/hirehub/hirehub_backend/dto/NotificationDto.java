package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private String relatedEntityType;
    private Long relatedEntityId;
    private Boolean isRead;
    private Boolean isEmailSent;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}

