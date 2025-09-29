package com.hirehub.hirehub_backend.controller;

import com.hirehub.hirehub_backend.config.JwtProvider;
import com.hirehub.hirehub_backend.dto.ProfileDto;
import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.service.ProfileService;
import com.hirehub.hirehub_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Autowired
    ProfileService profileService;
    @Autowired
    UserService userService;

    @GetMapping("common/get/{id}")
    public ResponseEntity<ProfileDto> getProfileById(@PathVariable Long id)throws Exception
    {
        return  new ResponseEntity<>(profileService.getProfile(id), HttpStatus.OK);
    }

    @PutMapping("common/update")
    public ResponseEntity<ProfileDto> updateProfile(
            @RequestHeader("Authorization") String jwt,
            @RequestBody ProfileDto profileDto) throws Exception {

        // Extract email from JWT
        String email = JwtProvider.getEmailFromToken(jwt);

        // Update only this user's profile
        ProfileDto updatedProfile = profileService.updateProfileByEmail(email, profileDto);

        return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
    }
    @GetMapping("common/getAll")
    public ResponseEntity<List<ProfileDto>> getAllProfile()throws Exception
    {
        return  new ResponseEntity<>(profileService.getAllProfile(), HttpStatus.OK);
    }


}
