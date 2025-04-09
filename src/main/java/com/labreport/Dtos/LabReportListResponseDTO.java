package com.labreport.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

//list of all reports for patient
@Data
@AllArgsConstructor
public class LabReportListResponseDTO {
    private List<LabReportResponseDTO> lab_reports;
}