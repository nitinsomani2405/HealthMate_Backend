package com.appointment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;


//dto for providing all appointments to patient group by doctor
@Data
@AllArgsConstructor
public class DoctorAppointmentGroupDTO {
    private UUID doctor_id;
    private String doctor_name;
    private List<SingleAppointmentDTO> appointments;
}
