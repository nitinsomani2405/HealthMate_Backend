package com.prescription.Repository;

import com.prescription.Models.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    // This fetches all prescriptions prescribed given patient
    List<Prescription> findAllByPatient_PatientId(UUID patientId);

    //This fetches all prescriptions prescribed by doctor
    List<Prescription> findAllByDoctorDoctorId(UUID doctorId);

}
