package com.hirehub.hirehub_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDto {
    private Long id;
    private Long participant1Id;
    private String participant1Name;
    private String participant1Email;
    private Long participant2Id;
    private String participant2Name;
    private String participant2Email;
    private Long jobId;
    private String jobTitle;
    private LocalDateTime lastMessageAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MessageDto> messages;
    private Long unreadCount; // Number of unread messages for the current user
}






