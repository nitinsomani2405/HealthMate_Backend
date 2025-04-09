package com.labreport.Repository;

import com.labreport.Models.LabReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabReportRepository extends JpaRepository<LabReport, UUID> {
    List<LabReport> findByPatient_PatientId(UUID patientId);

    List<LabReport> findByDoctor_DoctorId(UUID doctorId);
}

