package com.hirehub.hirehub_backend.entity;
import com.hirehub.hirehub_backend.dto.UserResponse;
import com.hirehub.hirehub_backend.enums.Gender;
import com.hirehub.hirehub_backend.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
