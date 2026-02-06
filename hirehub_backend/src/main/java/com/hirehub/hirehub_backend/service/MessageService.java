package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ConversationDto;
import com.hirehub.hirehub_backend.dto.MessageDto;

import java.util.List;

public interface MessageService {
    // Create or get existing conversation between two users
    ConversationDto createOrGetConversation(Long userId1, Long userId2, Long jobId) throws Exception;

    // Send a message
    MessageDto sendMessage(Long senderId, Long receiverId, String content, Long jobId) throws Exception;

    // Get all conversations for a user
    List<ConversationDto> getUserConversations(Long userId) throws Exception;

    // Get messages in a conversation
    List<MessageDto> getConversationMessages(Long conversationId, Long userId) throws Exception;

    // Mark messages as read
    void markMessagesAsRead(Long conversationId, Long userId) throws Exception;

    // Get unread message count for a user
    Long getUnreadMessageCount(Long userId) throws Exception;

    // Delete a message (soft delete or hard delete)
    void deleteMessage(Long messageId, Long userId) throws Exception;
}




