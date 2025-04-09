package com.appointment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//dto for providing all appointments to doctor group by patient
public class PatientAppointmentGroupDTO {
    private UUID patientId;
    private String patientName;
    private List<SingleAppointmentDTO> appointments;
}
