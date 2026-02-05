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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String projectName;
    private String description;
    private String technologies; // Comma-separated or list of technologies used
    private String projectUrl; // GitHub link, live demo URL, etc.
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean currentlyWorking;
}






