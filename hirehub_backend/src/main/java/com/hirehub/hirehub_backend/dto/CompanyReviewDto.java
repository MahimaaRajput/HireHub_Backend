package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.CompanyReview;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyReviewDto {
    private Long id;
    private Long companyId;
    private Long reviewerId;
    private String reviewerName;
    private Integer rating; // 1-5
    private String title;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public CompanyReview toEntity() {
        CompanyReview review = new CompanyReview();
        review.setId(this.id);
        review.setRating(this.rating);
        review.setTitle(this.title);
        review.setComment(this.comment);
        // company and reviewer will be set separately in service
        return review;
    }
}



