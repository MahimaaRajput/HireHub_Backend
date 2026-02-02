package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(Long userId, NotificationDto notificationDto) throws Exception;
    List<NotificationDto> getUserNotifications(Long userId);
    List<NotificationDto> getUnreadNotifications(Long userId);
    NotificationDto markAsRead(Long notificationId, Long userId) throws Exception;
    void markAllAsRead(Long userId);
    Long getUnreadCount(Long userId);
    void sendApplicationStatusNotification(Long userId, String jobTitle, String status) throws Exception;
    void sendNewJobNotification(Long userId, String jobTitle, Long jobId) throws Exception;
    void sendJobAlertNotification(Long userId, String alertName, List<Long> jobIds) throws Exception;
}


