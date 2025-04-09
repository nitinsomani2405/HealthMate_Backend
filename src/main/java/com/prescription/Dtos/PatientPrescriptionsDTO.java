package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

//(this one used to give all prescriptions prescribe to one patient by particular doctor)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientPrescriptionsDTO {
    private UUID patientId;
    private String patientName;
    private List<PrescriptionDTO> prescriptions;
}