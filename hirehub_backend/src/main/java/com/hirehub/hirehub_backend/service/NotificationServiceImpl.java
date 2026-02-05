package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.NotificationDto;
import com.hirehub.hirehub_backend.entity.Notification;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.repository.NotificationRepository;
import com.hirehub.hirehub_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserNotificationPreferencesService preferencesService;
    
    @Override
    public NotificationDto createNotification(Long userId, NotificationDto notificationDto) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(notificationDto.getTitle());
        notification.setMessage(notificationDto.getMessage());
        notification.setType(notificationDto.getType());
        notification.setRelatedEntityType(notificationDto.getRelatedEntityType());
        notification.setRelatedEntityId(notificationDto.getRelatedEntityId());
        notification.setIsRead(false);
        notification.setIsEmailSent(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send email if user has email notifications enabled
        if (preferencesService.isEmailNotificationEnabled(userId, notificationDto.getType())) {
            try {
                emailService.sendNotificationEmail(user.getEmail(), notificationDto.getTitle(), notificationDto.getMessage());
                savedNotification.setIsEmailSent(true);
                notificationRepository.save(savedNotification);
            } catch (Exception e) {
                // Log error but don't fail notification creation
                System.err.println("Failed to send email notification: " + e.getMessage());
            }
        }
        
        return toDto(savedNotification);
    }
    
    @Override
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public NotificationDto markAsRead(Long notificationId, Long userId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exception("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new Exception("You don't have permission to access this notification");
        }
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        Notification updatedNotification = notificationRepository.save(notification);
        return toDto(updatedNotification);
    }
    
    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    @Override
    public void sendApplicationStatusNotification(Long userId, String jobTitle, String status) throws Exception {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setTitle("Application Status Update");
        notificationDto.setMessage("Your application for " + jobTitle + " has been updated to: " + status);
        notificationDto.setType("APPLICATION_STATUS");
        notificationDto.setRelatedEntityType("APPLICATION");
        createNotification(userId, notificationDto);
    }
    
    @Override
    public void sendNewJobNotification(Long userId, String jobTitle, Long jobId) throws Exception {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setTitle("New Job Available");
        notificationDto.setMessage("A new job matching your profile: " + jobTitle);
        notificationDto.setType("NEW_JOB");
        notificationDto.setRelatedEntityType("JOB");
        notificationDto.setRelatedEntityId(jobId);
        createNotification(userId, notificationDto);
    }
    
    @Override
    public void sendJobAlertNotification(Long userId, String alertName, List<Long> jobIds) throws Exception {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setUserId(userId);
        notificationDto.setTitle("Job Alert: " + alertName);
        notificationDto.setMessage("Found " + jobIds.size() + " new job(s) matching your alert criteria");
        notificationDto.setType("JOB_ALERT");
        notificationDto.setRelatedEntityType("JOB_ALERT");
        createNotification(userId, notificationDto);
    }
    
    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getRelatedEntityType(),
                notification.getRelatedEntityId(),
                notification.getIsRead(),
                notification.getIsEmailSent(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}





