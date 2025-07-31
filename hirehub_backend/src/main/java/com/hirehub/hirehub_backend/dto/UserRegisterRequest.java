package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.User;
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
public class UserRegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private Gender gender;
    private Role role;


    public User toEntity()
    {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .fullName(this.fullName)
                .gender(this.gender)
                .role(this.role)
                .build();
    }

}
