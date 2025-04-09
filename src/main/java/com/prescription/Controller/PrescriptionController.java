package com.prescription.Controller;

import com.auth.Repository.SessionRepository;
import com.prescription.Dtos.*;
import com.prescription.Service.PrescriptionService;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    //ADD PRESCRIPTION BY DOCTOR
    @PostMapping("/prescriptions")
    public ResponseEntity<AddPrescriptionResponseDTO> addPrescription(
            @RequestHeader("Authorization") String authorization,
            @RequestBody AddPrescriptionRequestDTO request
    ) throws Exception {
        String accessToken = authorization.substring(7); // Remove "Bearer "
        AddPrescriptionResponseDTO response = prescriptionService.addPrescription(accessToken, request);
        return ResponseEntity.ok(response);
    }

    //ALL PRESCRIPTIONS PRESCRIBED TO  PATIENT GROUP BY DOCTOR
    @GetMapping("/patient/prescriptions")
    public ResponseEntity<GetAllPatientPrescriptionsResponseDTO> getAllPrescriptions(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");

        UUID userId = sessionRepository.findUserIdByAccessToken(token);
        Patient patient = patientRepository.findByPatientId(userId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        UUID patientId = patient.getPatientId();

        List<DoctorPrescriptionsDTO> prescriptions = prescriptionService.getPrescriptionsGroupedByDoctor(patientId);
        return ResponseEntity.ok(new GetAllPatientPrescriptionsResponseDTO(prescriptions));
    }


    //ALL PRESCRIPTIONS PRESCRIBED BY DOCTOR GROUP BY PATIENT
    @GetMapping("/doctor/prescriptions")
    public ResponseEntity<GetAllDoctorPrescriptionsResponseDTO> getAllPrescriptionsByDoctor(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        UUID userId = sessionRepository.findUserIdByAccessToken(token);
        Doctor doctor = doctorRepository.findByDoctorId(userId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return ResponseEntity.ok(prescriptionService.getPrescriptionsGroupedByPatient(doctor.getDoctorId()));
    }
}
