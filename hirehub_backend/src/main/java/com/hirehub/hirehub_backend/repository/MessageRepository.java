package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find all messages in a conversation
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    // Count unread messages for a user in a conversation
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.receiver.id = :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    // Count all unread messages for a user across all conversations
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = false")
    Long countAllUnreadMessagesForUser(@Param("userId") Long userId);

    // Find unread messages for a user in a conversation
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.receiver.id = :userId AND m.isRead = false ORDER BY m.createdAt ASC")
    List<Message> findUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}



