package com.hirehub.hirehub_backend.service;

import com.hirehub.hirehub_backend.dto.ProfileDto;
import com.hirehub.hirehub_backend.entity.Profile;
import com.hirehub.hirehub_backend.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Long createProfile(String email) {
        Profile profile = new Profile();
        profile.setEmail(email);
        profile.setSkills(new ArrayList<>());
        profile.setExperiences(new ArrayList<>());
        profile.setCertifications(new ArrayList<>());
        profileRepository.save(profile);
        return profile.getId();
    }

    @Override
    public ProfileDto getProfile(Long id) throws Exception {
        return profileRepository.findById(id).orElseThrow(() -> new Exception("Profile not found with this id")).toDto();
    }

    @Override
    public ProfileDto updateProfileByEmail(String email, ProfileDto profileDto) throws Exception {
        Profile existingProfile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Profile not found for this user"));

        // Update only allowed fields
        existingProfile.setJobTitle(profileDto.getJobTitle());
        existingProfile.setLocation(profileDto.getLocation());
        existingProfile.setAbout(profileDto.getAbout());
        existingProfile.setSkills(profileDto.getSkills());
        existingProfile.setExperiences(profileDto.getExperiences());
        existingProfile.setCertifications(profileDto.getCertifications());

        profileRepository.save(existingProfile);
        return existingProfile.toDto();
    }

}
