package com.hirehub.hirehub_backend.dto;

import com.hirehub.hirehub_backend.entity.User;
import com.hirehub.hirehub_backend.enums.Gender;
import com.hirehub.hirehub_backend.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequest {
    @NotBlank(message = "username is required ")
    private String fullName;
    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;
    @NotBlank(message = "password is required")
    private String password;
    @NotNull(message = "gender is required")
    private Gender gender;
   @NotNull(message = "role is required field")
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
