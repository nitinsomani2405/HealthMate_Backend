package com.appointment.Dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
//dto used in doctorappoinment and patientappointment group and work as single appointment info
@AllArgsConstructor
public class SingleAppointmentDTO {
    private UUID appointment_id;
    private LocalDate appointment_date;
    private LocalTime appointment_time;
    private String status;
    private String reason;
}
