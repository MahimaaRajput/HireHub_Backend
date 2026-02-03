package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    // Find interviews by application
    List<Interview> findByApplicationApplicantIdOrderByScheduledAtDesc(Long applicantId);

    // Find interviews by candidate
    @Query("SELECT i FROM Interview i WHERE i.candidate.id = :candidateId ORDER BY i.scheduledAt DESC")
    List<Interview> findByCandidateId(@Param("candidateId") Long candidateId);

    // Find interviews by recruiter (scheduled by)
    @Query("SELECT i FROM Interview i WHERE i.scheduledBy.id = :recruiterId ORDER BY i.scheduledAt DESC")
    List<Interview> findByRecruiterId(@Param("recruiterId") Long recruiterId);

    // Find upcoming interviews for a candidate
    @Query("SELECT i FROM Interview i WHERE i.candidate.id = :candidateId AND i.scheduledAt >= :now AND i.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY i.scheduledAt ASC")
    List<Interview> findUpcomingInterviewsForCandidate(@Param("candidateId") Long candidateId, @Param("now") LocalDateTime now);

    // Find upcoming interviews for a recruiter
    @Query("SELECT i FROM Interview i WHERE i.scheduledBy.id = :recruiterId AND i.scheduledAt >= :now AND i.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY i.scheduledAt ASC")
    List<Interview> findUpcomingInterviewsForRecruiter(@Param("recruiterId") Long recruiterId, @Param("now") LocalDateTime now);

    // Find interviews by status
    List<Interview> findByStatusOrderByScheduledAtDesc(String status);

    // Find interviews scheduled between dates
    @Query("SELECT i FROM Interview i WHERE i.scheduledAt BETWEEN :startDate AND :endDate ORDER BY i.scheduledAt ASC")
    List<Interview> findInterviewsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}

