package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
//add prescription response dto
public class AddPrescriptionResponseDTO {
    private String message;
    private UUID prescription_id;
}
