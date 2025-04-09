package com.prescription.Models;

import com.prescription.Dtos.PrescriptionMedicineDTO;
import com.prescription.Utils.JsonConverter;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "prescription_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID prescriptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "prescribed_at", nullable = false)
    private Instant prescribedAt = Instant.now();



    @Column(name = "medicines", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = JsonConverter.class)
    private List<PrescriptionMedicineDTO> medicines;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;


}
