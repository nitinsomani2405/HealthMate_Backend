package com.labreport.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

//one lab report dto for patient
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabReportPatientResponseDTO {
    private UUID reportId;
    private String doctorName;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}

