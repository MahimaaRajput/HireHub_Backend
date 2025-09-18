package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job,Long> {
    List<Job> findByPostedBy(Long PostedBy);
}
