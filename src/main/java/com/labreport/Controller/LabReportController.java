package com.labreport.Controller;


import com.labreport.Dtos.LabReportListResponseDTO;
import com.labreport.Dtos.LabReportResponseDTO;
import com.labreport.Dtos.LabReportUploadResponseDTO;
import com.labreport.Service.LabReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/lab-reports")
@RequiredArgsConstructor
public class LabReportController {

    private final LabReportService labReportService;


    //API FOR UPLOADING PDF FILE(URL) IN SUPABASE STORAGE AS A BUSCKET LAP-REPORTS BY DOCTOR
    @PostMapping("/upload")
    public ResponseEntity<LabReportUploadResponseDTO> uploadLabReport(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("patient_id") UUID patientId,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Extract the access token from Bearer <token>
        String token = authorization.replace("Bearer ", "").trim();

        LabReportUploadResponseDTO response = labReportService.uploadLabReport(token, patientId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient")
    public ResponseEntity<LabReportListResponseDTO> getLabReportsForPatient(
            @RequestHeader("Authorization") String authorization) throws AccessDeniedException {

        String accessToken = authorization.replace("Bearer ", "").trim();
        LabReportListResponseDTO response = labReportService.getAllLabReportsForPatient(accessToken);
        return ResponseEntity.ok(response);
    }

}
