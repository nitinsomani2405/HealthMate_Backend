package com.miscellaneous.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedPatientDTO {
    private UUID patientId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String dob;
}
