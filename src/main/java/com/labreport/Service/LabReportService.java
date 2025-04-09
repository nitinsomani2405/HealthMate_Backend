package com.labreport.Service;


import com.auth.Models.user;
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;
import com.labreport.Dtos.*;
import com.labreport.Models.LabReport;
import com.labreport.Repository.LabReportRepository;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabReportService {

    private final SessionRepository sessionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final LabReportRepository labReportRepository;
    private final SupabaseService supabaseService;
    private final UserRepository userRepository;

    // FOR UPLOADING PDF FILE(URL) IN SUPABASE STORAGE AS A BUSCKET LAP-REPORTS BY DOCTOR
    @Transactional
    public LabReportUploadResponseDTO uploadLabReport(String accessToken, UUID patientId, MultipartFile file) throws IOException {
        // 1. Get doctorId from access token
        UUID doctorId = sessionRepository.findUserIdByAccessToken(accessToken);

        // 2. Get doctor
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // 3. Validate patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // 4. Upload PDF to Supabase
        String fileUrl = supabaseService.uploadLabReport(doctorId, patientId, file);

        // 5. Save record in DB
        LabReport report = new LabReport();
        report.setDoctor(doctor);
        report.setPatient(patient);
        report.setFileUrl(fileUrl);
        labReportRepository.save(report);

        // 6. Return success response
        return new LabReportUploadResponseDTO("Lab report uploaded successfully", fileUrl);
    }

    public GetAllLabReportsByPatientResponseDTO getAllLabReportsForPatient(String accessToken) throws AccessDeniedException {
        UUID userId = sessionRepository.findUserIdByAccessToken(accessToken);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid access token");
        }

        user user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.getRole().equalsIgnoreCase("PATIENT")) {
            throw new AccessDeniedException("Only patients can access lab reports.");
        }
        List<LabReport> labReports = labReportRepository.findByPatient_PatientId(userId);

        List<LabReportPatientResponseDTO> dtos = labReports.stream().map(report -> {
            UUID doctorId = report.getDoctor().getDoctorId();
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new NoSuchElementException("Doctor not found"));

            return new LabReportPatientResponseDTO(
                    report.getReportId(),
                    doctor.getUser().getFullName(),
                    report.getFileUrl(),
                    report.getUploaded_at()
            );
        }).toList();

        return new GetAllLabReportsByPatientResponseDTO(dtos);
    }

    public GetAllLabReportsByDoctorResponseDTO getAllLabReportsForDoctor(String accessToken) throws AccessDeniedException {
        // Step 1: Get userId from session repository using access token
        UUID userId = sessionRepository.findUserIdByAccessToken(accessToken);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid access token");
        }

        // Step 2: Fetch user and check if doctor
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.getRole().equalsIgnoreCase("DOCTOR")) {
            throw new AccessDeniedException("Only doctors can access this data.");
        }



        // Step 3: Fetch all lab reports uploaded by this doctor
        List<LabReport> labReports = labReportRepository.findByDoctor_DoctorId(userId);

        // Step 4: Convert to response DTOs
        List<LabReportDoctorResponseDTO> dtos = labReports.stream().map(report -> {
            UUID patientId = report.getPatient().getPatientId();
            Patient patient = report.getPatient();

            return new LabReportDoctorResponseDTO(
                    report.getReportId(),
                    patient.getUser().getFullName(),
                    report.getFileUrl(),
                    report.getUploaded_at()
            );
        }).toList();

        return new GetAllLabReportsByDoctorResponseDTO(dtos);
    }


    public void deleteLabReport(UUID reportId, String accessToken) throws AccessDeniedException {
        UUID userId = sessionRepository.findUserIdByAccessToken(accessToken);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid access token");
        }

        user user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (!user.getRole().equalsIgnoreCase("DOCTOR")) {
            throw new AccessDeniedException("Only doctors can delete lab reports.");
        }



        LabReport report = labReportRepository.findById(reportId)
                .orElseThrow(() -> new NoSuchElementException("Lab report not found"));

        if (!report.getDoctor().getDoctorId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this report.");
        }

        // Delete the file from Supabase
        supabaseService.deleteFile(report.getFileUrl());


        // Delete the lab report record from database
        labReportRepository.delete(report);
    }



}
