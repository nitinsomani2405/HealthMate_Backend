package com.appointment.Dtos;

import com.appointment.Models.Appointment.Status;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookAppointmentResponseDTO {
    private String message;
    private UUID appointmentId;
    private Status status;
}
