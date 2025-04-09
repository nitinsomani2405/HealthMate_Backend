package com.prescription.Dtos;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.List;
import java.util.UUID;


//add prescription request dto
@Data
public class AddPrescriptionRequestDTO {
    private UUID patient_id;
    private List<PrescriptionMedicineDTO> medicines;
    private String notes;
}
