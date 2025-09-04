package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ProfileDto;

public interface ProfileService {
    Long createProfile (String email);
    ProfileDto getProfile(Long id) throws Exception;
    ProfileDto updateProfileByEmail(String email, ProfileDto profileDto) throws Exception;
}
