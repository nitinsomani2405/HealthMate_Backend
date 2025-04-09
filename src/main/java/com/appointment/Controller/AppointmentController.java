package com.appointment.Controller;

import com.appointment.Dtos.BookAppointmentRequestDTO;
import com.appointment.Dtos.BookAppointmentResponseDTO;
import com.appointment.Service.AppointmentService;
import com.appointment.Dtos.DoctorAppointmentGroupDTO;
import com.appointment.Dtos.PatientAppointmentGroupDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    //BOOK APPOINTMENT
    @PostMapping("/book-appointment")
    public ResponseEntity<BookAppointmentResponseDTO> bookAppointment(
            @RequestHeader("Authorization") String token,
            @RequestBody BookAppointmentRequestDTO request
    ) {
        BookAppointmentResponseDTO response = appointmentService.bookAppointment(token, request);
        return ResponseEntity.ok(response);
    }


    //CANCEL APPOINTMENT
    @PutMapping("/cancel-appointment/{appointmentId}")
    public ResponseEntity<BookAppointmentResponseDTO> cancelAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID appointmentId
    ) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(token, appointmentId));
    }

    //CONFIRM APPOINTMENT
    @PutMapping("/confirm-appointment/{appointmentId}")
    public ResponseEntity<BookAppointmentResponseDTO> confirmAppointment(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID appointmentId
    ) {
        return ResponseEntity.ok(appointmentService.confirmAppointment(token, appointmentId));
    }

    //ALL APPOINTMENTS FOR PATIENT GROUP BY DOCTOR
    @GetMapping("/patient/appointments")
    public ResponseEntity<Map<String, Object>> getAppointmentsForPatient(
            @RequestHeader("Authorization") String token) {
        System.out.println("Token: "+token);
        List<DoctorAppointmentGroupDTO> appointments= appointmentService.getAppointmentsForPatient(token);

        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }

    //ALL APPOINTMENTS FOR DOCTOR GROUP BY PATIENT
    @GetMapping("/doctor/appointments")
    public ResponseEntity<Map<String, Object>> getAppointmentsForDoctor(
            @RequestHeader("Authorization") String token) {
        List<PatientAppointmentGroupDTO> appointments = appointmentService.getAppointmentsGroupedByPatient(token);
        Map<String, Object> response = new HashMap<>();
        response.put("appointments", appointments);
        return ResponseEntity.ok(response);
    }





}
