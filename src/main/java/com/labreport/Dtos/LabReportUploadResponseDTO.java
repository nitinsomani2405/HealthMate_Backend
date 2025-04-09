package com.labreport.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LabReportUploadResponseDTO {
    private String message;
    private String file_url;
}
