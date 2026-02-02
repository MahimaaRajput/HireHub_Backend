package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.JobView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobViewRepository extends JpaRepository<JobView, Long> {
    Optional<JobView> findByUserIdAndJobId(Long userId, Long jobId);
    
    @Query("SELECT jv FROM JobView jv WHERE jv.user.id = :userId ORDER BY jv.viewedAt DESC")
    List<JobView> findRecentViewsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(jv) FROM JobView jv WHERE jv.job.id = :jobId")
    Long countViewsByJobId(@Param("jobId") Long jobId);
}

