package com.profile.Dtos;

import lombok.Data;

@Data
public class PostProfileRequest {

    // Common field
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String dateOfBirth;
    // Doctor-specific fields (ignored for patients)
    private String speciality;
    private String clinicName;
}
