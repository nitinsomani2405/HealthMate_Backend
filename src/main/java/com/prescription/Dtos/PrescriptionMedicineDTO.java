package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//medicine info dto
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionMedicineDTO {
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
    private String instructions;
}
