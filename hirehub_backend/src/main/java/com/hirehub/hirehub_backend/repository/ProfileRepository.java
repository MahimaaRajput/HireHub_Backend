package com.hirehub.hirehub_backend.repository;

import com.hirehub.hirehub_backend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile,Long> {
    Optional<Profile> findByEmail(String email);
}
