package com.hirehub.hirehub_backend.entity;
import com.hirehub.hirehub_backend.dto.UserResponse;
import com.hirehub.hirehub_backend.enums.Gender;
import com.hirehub.hirehub_backend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
//    @JoinColumn(name = "profile_id")
    private Profile profile;

    // Email verification fields
    @Builder.Default
    private Boolean emailVerified = false;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationTokenExpiry;

    // Phone verification fields
    private String phoneNumber;
    @Builder.Default
    private Boolean phoneVerified = false;
    private String phoneVerificationOtp;
    private LocalDateTime phoneVerificationOtpExpiry;

    // Two-factor authentication fields
    @Builder.Default
    private Boolean twoFactorEnabled = false;
    private String twoFactorSecret;

    // Password reset fields
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiry;


    public UserResponse toResponse() {
        return UserResponse.builder()
                .email(this.email)
                .fullName(this.fullName)
                .gender(this.gender)
                .role(this.role)
                .profileId(this.profile != null ? this.profile.getId() : null)
                .build();
    }


}
