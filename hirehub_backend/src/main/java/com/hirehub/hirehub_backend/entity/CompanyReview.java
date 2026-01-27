package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.CompanyReviewDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company; // Company being reviewed
    
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer; // User who wrote the review
    
    private Integer rating; // Rating from 1 to 5
    private String title; // Review title
    private String comment; // Review comment/description
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public CompanyReviewDto toDto() {
        return new CompanyReviewDto(
            this.id,
            this.company != null ? this.company.getId() : null,
            this.reviewer != null ? this.reviewer.getId() : null,
            this.reviewer != null ? this.reviewer.getFullName() : null,
            this.rating,
            this.title,
            this.comment,
            this.createdAt,
            this.updatedAt
        );
    }
}


