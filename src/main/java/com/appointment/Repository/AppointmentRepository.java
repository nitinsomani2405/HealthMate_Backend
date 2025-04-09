package com.appointment.Repository;

import com.appointment.Models.Appointment;
import com.profile.Models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByPatientUserId(UUID patientId);
    List<Appointment> findByDoctorUserId(UUID doctorId);

    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.doctorId = :doctorId")
    List<Patient> findDistinctPatientsByDoctorId(@Param("doctorId") UUID doctorId);

}
