package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
}
