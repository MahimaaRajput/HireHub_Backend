package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    // Find conversation between two specific users
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.participant1.id = :userId1 AND c.participant2.id = :userId2) OR " +
           "(c.participant1.id = :userId2 AND c.participant2.id = :userId1)")
    Optional<Conversation> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Find all conversations for a user
    @Query("SELECT c FROM Conversation c WHERE c.participant1.id = :userId OR c.participant2.id = :userId ORDER BY c.lastMessageAt DESC NULLS LAST, c.updatedAt DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") Long userId);

    // Find conversation by job and participants
    @Query("SELECT c FROM Conversation c WHERE c.job.id = :jobId AND " +
           "((c.participant1.id = :userId1 AND c.participant2.id = :userId2) OR " +
           "(c.participant1.id = :userId2 AND c.participant2.id = :userId1))")
    Optional<Conversation> findConversationByJobAndUsers(@Param("jobId") Long jobId, 
                                                          @Param("userId1") Long userId1, 
                                                          @Param("userId2") Long userId2);
}

