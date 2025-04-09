package com.prescription.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


//one prescription data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDTO {
    private UUID prescription_id;
    private LocalDateTime prescribed_at;
    private List<PrescriptionMedicineDTO> medicines;
    private String notes;
}
