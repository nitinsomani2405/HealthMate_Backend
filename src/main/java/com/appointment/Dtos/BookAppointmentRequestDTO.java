package com.appointment.Dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentRequestDTO {
    private UUID doctorId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
}
