package com.labreport.Controller;


import com.labreport.Dtos.GetAllLabReportsByDoctorResponseDTO;
import com.labreport.Dtos.GetAllLabReportsByPatientResponseDTO;
import com.labreport.Dtos.LabReportUploadResponseDTO;
import com.labreport.Service.LabReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
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

    //API TO FETCH ALL LAB REPORTS OF PATIENT WHICH UPLOADED BY DOCTORS
    @GetMapping("/patient")
    public ResponseEntity<GetAllLabReportsByPatientResponseDTO> getLabReportsForPatient(
            @RequestHeader("Authorization") String authorization) throws AccessDeniedException {

        String accessToken = authorization.replace("Bearer ", "").trim();
        GetAllLabReportsByPatientResponseDTO response = labReportService.getAllLabReportsForPatient(accessToken);
        return ResponseEntity.ok(response);
    }

    // Doctors can see all reports they uploaded.
    @GetMapping("/doctor")
    public ResponseEntity<GetAllLabReportsByDoctorResponseDTO> getLabReportsForDoctor(
            @RequestHeader("Authorization") String authorization) throws AccessDeniedException {
        String accessToken = authorization.replace("Bearer ", "").trim();
        GetAllLabReportsByDoctorResponseDTO response =labReportService.getAllLabReportsForDoctor(accessToken);
        return ResponseEntity.ok(response);
    }

    // Doctor can delete their uploaded reports
    @DeleteMapping("/{report_id}")
    public ResponseEntity<Map<String, String>> deleteLabReport(
            @PathVariable("report_id") UUID reportId,
            @RequestHeader("Authorization") String authorization) throws AccessDeniedException {

        String accessToken = authorization.replace("Bearer ", "").trim();
        labReportService.deleteLabReport(reportId, accessToken);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Lab report deleted successfully");
        return ResponseEntity.ok(response);
    }


}
