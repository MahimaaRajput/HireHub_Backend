package com.hirehub.hirehub_backend.entity;

import com.hirehub.hirehub_backend.dto.CompanyDto;
import com.hirehub.hirehub_backend.enums.VerificationStatus;
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
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String companyName; // Company name (unique)
    
    private String logoUrl; // URL or path to company logo
    private String description; // Company description
    private String website; // Company website URL
    private String size; // Company size (e.g., "1-50", "51-200", "201-500", "500+")
    private String industry; // Industry sector (e.g., "IT", "Finance", "Healthcare")
    
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus; // Company verification status
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs; // Jobs posted by this company
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // User who owns/registered this company
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    public CompanyDto toDto() {
        return new CompanyDto(
            this.id,
            this.companyName,
            this.logoUrl,
            this.description,
            this.website,
            this.size,
            this.industry,
            this.verificationStatus,
            this.owner != null ? this.owner.getId() : null,
            this.createdAt,
            this.updatedAt
        );
    }
}

