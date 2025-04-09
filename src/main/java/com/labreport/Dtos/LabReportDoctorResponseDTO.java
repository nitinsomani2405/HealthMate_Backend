package com.labreport.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabReportDoctorResponseDTO {
    private UUID report_id;
    private String patient_name;
    private String file_url;
    private LocalDateTime uploaded_at;
}
