package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


//this dto used to give patient all prescription group by doctor (this one used to create all prescription of one doctor)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorPrescriptionsDTO {
    private UUID doctor_id;                         // Doctor's UUID
    private String doctor_name;                     // Doctor's full name
    private List<PrescriptionDTO> prescriptions;    // Prescriptions written by this doctor
}
