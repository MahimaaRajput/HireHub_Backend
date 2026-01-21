package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByPostedBy(Long PostedBy);
    
    // Search jobs by keywords in job title, company, description, or skills
    @Query("SELECT j FROM Job j WHERE " +
           "LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.company) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS (SELECT 1 FROM j.skillsRequired s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Job> searchJobsByKeyword(@Param("keyword") String keyword);
    
    // Filter jobs by multiple criteria
    @Query("SELECT j FROM Job j WHERE " +
           "(:minSalary IS NULL OR j.packageOffered >= :minSalary) AND " +
           "(:maxSalary IS NULL OR j.packageOffered <= :maxSalary) AND " +
           "(:experience IS NULL OR LOWER(j.experience) LIKE LOWER(CONCAT('%', :experience, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR LOWER(j.jobType) LIKE LOWER(CONCAT('%', :jobType, '%'))) AND " +
           "(:startDate IS NULL OR j.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR j.createdAt <= :endDate) AND " +
           "j.jobStatus = 'OPEN'")
    List<Job> filterJobs(
            @Param("minSalary") Long minSalary,
            @Param("maxSalary") Long maxSalary,
            @Param("experience") String experience,
            @Param("location") String location,
            @Param("jobType") String jobType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    // Find all jobs sorted by different criteria
    List<Job> findAllByOrderByCreatedAtDesc();
    List<Job> findAllByOrderByCreatedAtAsc();
    List<Job> findAllByOrderByPackageOfferedDesc();
    List<Job> findAllByOrderByPackageOfferedAsc();
}
