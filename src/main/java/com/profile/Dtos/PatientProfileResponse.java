package com.profile.Dtos;
import lombok.*;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private String role;
    private String dateOfBirth;
    private Boolean profileCompleted;
}
