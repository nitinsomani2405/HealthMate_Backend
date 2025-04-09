package com.miscellaneous.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


//dto containing doctor all info of one doctor basically join of user and doctor
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorResponseDTO {
    private UUID doctorId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String speciality;
    private String clinicName;
}