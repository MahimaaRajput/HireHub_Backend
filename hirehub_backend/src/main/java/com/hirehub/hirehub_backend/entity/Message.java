package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // User who sent the message

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // User who receives the message

    @Column(length = 5000)
    private String content; // Message content

    private Boolean isRead = false; // Whether the message has been read

    private LocalDateTime readAt; // Timestamp when message was read

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}



