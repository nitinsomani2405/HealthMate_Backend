package com.prescription.Service;

import com.prescription.Dtos.*;
import com.prescription.Models.Prescription;
import com.prescription.Repository.PrescriptionRepository;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import com.auth.Service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final TokenService tokenService;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;

    //ADD PRESCRIPTION BY DOCTOR
    @Transactional
    public AddPrescriptionResponseDTO addPrescription(String accessToken, AddPrescriptionRequestDTO request) throws Exception {
        UUID doctorId = tokenService.getUserIdFromAccessToken(accessToken);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepository.findById(request.getPatient_id())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);
        prescription.setMedicines(request.getMedicines());// No need to manually serialize now
        prescription.setNotes(request.getNotes());
        prescriptionRepository.save(prescription);

        return new AddPrescriptionResponseDTO("Prescription added successfully", prescription.getPrescriptionId());
    }


    //ALL PRESCRIPTIONS PRESCRIBED TO  PATIENT GROUP BY DOCTOR
    public List<DoctorPrescriptionsDTO> getPrescriptionsGroupedByDoctor(UUID patientId) {
        // Step 1: Get all prescriptions of the patient
        List<Prescription> prescriptions = prescriptionRepository.findAllByPatient_PatientId(patientId);

        // Step 2: Group prescriptions by doctor ID
        Map<UUID, List<Prescription>> groupedByDoctor = prescriptions.stream()
                .collect(Collectors.groupingBy(prescription -> prescription.getDoctor().getDoctorId()));

        // Step 3: Prepare the final DTO list
        List<DoctorPrescriptionsDTO> result = new ArrayList<>();

        for (Map.Entry<UUID, List<Prescription>> entry : groupedByDoctor.entrySet()) {
            UUID doctorId = entry.getKey();
            List<Prescription> doctorPrescriptions = entry.getValue();

            // Fetch doctor name using doctorId
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found for ID: " + doctorId));

            // Convert prescriptions to PrescriptionDTO
            List<PrescriptionDTO> prescriptionDataList = doctorPrescriptions.stream()
                    .map(prescription -> new PrescriptionDTO(
                            prescription.getPrescriptionId(),
                            LocalDateTime.ofInstant(prescription.getPrescribedAt(), ZoneId.systemDefault()),
                            prescription.getMedicines(),   // Assuming this returns List<MedicineDTO>
                            prescription.getNotes()
                    ))
                    .collect(Collectors.toList());

            // Create and add DoctorPrescriptionsDTO
            result.add(new DoctorPrescriptionsDTO(
                    doctorId,
                    doctor.getUser().getFullName(),
                    prescriptionDataList
            ));
        }

        return result;
    }

    //ALL PRESCRIPTIONS PRESCRIBED BY DOCTOR GROUP BY PATIENT
    public GetAllDoctorPrescriptionsResponseDTO getPrescriptionsGroupedByPatient(UUID doctorId) {
        List<Prescription> prescriptions = prescriptionRepository.findAllByDoctorDoctorId(doctorId);

        // Group prescriptions by Patient
        Map<UUID, List<Prescription>> groupedByPatient = prescriptions.stream()
                .collect(Collectors.groupingBy(p -> p.getPatient().getPatientId()));

        List<PatientPrescriptionsDTO> patientPrescriptions = new ArrayList<>();

        for (Map.Entry<UUID, List<Prescription>> entry : groupedByPatient.entrySet()) {
            UUID patientId = entry.getKey();
            List<Prescription> patientPrescriptionsList = entry.getValue();
            Patient patient = patientPrescriptionsList.get(0).getPatient();

            List<PrescriptionDTO> prescriptionDataList = patientPrescriptionsList.stream()
                    .map(p -> new PrescriptionDTO(
                            p.getPrescriptionId(),
                            LocalDateTime.ofInstant(p.getPrescribedAt(), ZoneId.systemDefault()),
                            p.getMedicines().stream()
                                    .map(m -> new PrescriptionMedicineDTO(
                                            m.getName(),
                                            m.getDosage(),
                                            m.getFrequency(),
                                            m.getDuration(),
                                            m.getInstructions()
                                    )).collect(Collectors.toList()),
                            p.getNotes()
                    )).collect(Collectors.toList());

            patientPrescriptions.add(new PatientPrescriptionsDTO(
                    patientId,
                    patient.getUser().getFullName() ,
                    prescriptionDataList
            ));
        }

        return new GetAllDoctorPrescriptionsResponseDTO(patientPrescriptions);
    }

}
