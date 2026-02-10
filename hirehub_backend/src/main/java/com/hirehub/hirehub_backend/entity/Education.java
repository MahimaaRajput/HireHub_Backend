package com.hirehub.hirehub_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String degree; // e.g., Bachelor's, Master's, PhD
    private String university;
    private String fieldOfStudy; // e.g., Computer Science, Engineering
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyStudying;
    private String description; // Optional: additional details
}









