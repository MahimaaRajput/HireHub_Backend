package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.enums.Gender;
import com.hirehub.hirehub_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;
    private String email;
    private Long id;
    private Role role;
    private Gender gender;
    private Long profileId;
}
