package com.labreport.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

//list of all reports for patient
@Data
@AllArgsConstructor
public class GetAllLabReportsByPatientResponseDTO {
    private List<LabReportPatientResponseDTO> lab_reports;
}