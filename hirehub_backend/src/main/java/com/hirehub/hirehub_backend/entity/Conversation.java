package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant1_id")
    private User participant1; // First participant (can be USER or RECRUITER)

    @ManyToOne
    @JoinColumn(name = "participant2_id")
    private User participant2; // Second participant (can be USER or RECRUITER)

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = true)
    private Job job; // Optional: Link conversation to a specific job application

    private LocalDateTime lastMessageAt; // Timestamp of the last message

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Helper method to get the other participant
    public User getOtherParticipant(Long userId) {
        if (participant1 != null && participant1.getId().equals(userId)) {
            return participant2;
        } else if (participant2 != null && participant2.getId().equals(userId)) {
            return participant1;
        }
        return null;
    }
}



