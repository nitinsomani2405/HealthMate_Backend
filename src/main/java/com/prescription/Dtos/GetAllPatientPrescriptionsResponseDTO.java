package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


//this dto to give all prescriptions of all doctors to particular patient
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAllPatientPrescriptionsResponseDTO {
    private List<DoctorPrescriptionsDTO> prescriptions;
}
