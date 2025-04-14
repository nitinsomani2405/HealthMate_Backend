package com.appointment.Controller;

import com.appointment.Models.Appointment;
import com.appointment.Service.AppointmentService;
import com.appointment.Dtos.BookAppointmentRequestDTO;
import com.appointment.Dtos.BookAppointmentResponseDTO;
import com.appointment.Dtos.DoctorAppointmentGroupDTO;
import com.appointment.Dtos.PatientAppointmentGroupDTO;
import com.appointment.Dtos.SingleAppointmentDTO;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private Appointment mockAppointment;
    private Doctor mockDoctor;
    private Patient mockPatient;
    private UUID mockAppointmentId;

    @BeforeEach
    void setUp() {
        mockAppointmentId = UUID.randomUUID();

        mockDoctor = new Doctor();
        mockDoctor.setDoctorId(UUID.randomUUID());

        mockPatient = new Patient();
        mockPatient.setPatientId(UUID.randomUUID());

        mockAppointment = new Appointment();
        mockAppointment.setAppointmentId(mockAppointmentId);
        mockAppointment.setDoctor(mockDoctor);
        mockAppointment.setPatient(mockPatient);
        mockAppointment.setAppointmentDate(LocalDate.now().plusDays(1));
        mockAppointment.setAppointmentTime(LocalTime.of(10, 0));
        mockAppointment.setReason("Regular checkup");
        mockAppointment.setStatus(Appointment.Status.CONFIRMED);
    }

    @Test
    void createAppointment_ValidData_ReturnsAppointment() throws Exception {
        BookAppointmentResponseDTO response = new BookAppointmentResponseDTO(
                "Appointment booked successfully",
                mockAppointmentId,
                Appointment.Status.CONFIRMED
        );

        when(appointmentService.bookAppointment(any(String.class), any(BookAppointmentRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/book-appointment")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doctorId\":\"" + mockDoctor.getDoctorId() + "\","
                                + "\"appointmentDate\":\"" + mockAppointment.getAppointmentDate() + "\","
                                + "\"appointmentTime\":\"" + mockAppointment.getAppointmentTime() + "\","
                                + "\"reason\":\"Regular checkup\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment booked successfully"))
                .andExpect(jsonPath("$.appointmentId").value(mockAppointmentId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void cancelAppointment_ValidId_ReturnsSuccess() throws Exception {
        BookAppointmentResponseDTO response = new BookAppointmentResponseDTO(
                "Appointment cancelled successfully",
                mockAppointmentId,
                Appointment.Status.CANCELLED
        );

        when(appointmentService.cancelAppointment(any(String.class), any(UUID.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/cancel-appointment/" + mockAppointmentId)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment cancelled successfully"))
                .andExpect(jsonPath("$.appointmentId").value(mockAppointmentId.toString()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void confirmAppointment_ValidId_ReturnsSuccess() throws Exception {
        BookAppointmentResponseDTO response = new BookAppointmentResponseDTO(
                "Appointment confirmed successfully",
                mockAppointmentId,
                Appointment.Status.CONFIRMED
        );

        when(appointmentService.confirmAppointment(any(String.class), any(UUID.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/confirm-appointment/" + mockAppointmentId)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment confirmed successfully"))
                .andExpect(jsonPath("$.appointmentId").value(mockAppointmentId.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void getPatientAppointments_ValidToken_ReturnsAppointments() throws Exception {
        SingleAppointmentDTO appointmentDTO = new SingleAppointmentDTO(
                mockAppointmentId,
                mockAppointment.getAppointmentDate(),
                mockAppointment.getAppointmentTime(),
                mockAppointment.getStatus().toString(),
                mockAppointment.getReason()
        );

        DoctorAppointmentGroupDTO groupDTO = new DoctorAppointmentGroupDTO(
                mockDoctor.getDoctorId(),
                "Dr. John Doe",
                List.of(appointmentDTO)
        );

        when(appointmentService.getAppointmentsForPatient(any(String.class)))
                .thenReturn(List.of(groupDTO));

        mockMvc.perform(get("/api/patient/appointments")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments[0].doctor_id").value(mockDoctor.getDoctorId().toString()))
                .andExpect(jsonPath("$.appointments[0].doctor_name").value("Dr. John Doe"))
                .andExpect(jsonPath("$.appointments[0].appointments[0].appointment_id").value(mockAppointmentId.toString()));
    }

    @Test
    void getDoctorAppointments_ValidToken_ReturnsAppointments() throws Exception {
        SingleAppointmentDTO appointmentDTO = new SingleAppointmentDTO(
                mockAppointmentId,
                mockAppointment.getAppointmentDate(),
                mockAppointment.getAppointmentTime(),
                mockAppointment.getStatus().toString(),
                mockAppointment.getReason()
        );

        PatientAppointmentGroupDTO groupDTO = new PatientAppointmentGroupDTO(
                mockPatient.getPatientId(),
                "Jane Doe",
                List.of(appointmentDTO)
        );

        when(appointmentService.getAppointmentsGroupedByPatient(any(String.class)))
                .thenReturn(List.of(groupDTO));

        mockMvc.perform(get("/api/doctor/appointments")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointments[0].patientId").value(mockPatient.getPatientId().toString()))
                .andExpect(jsonPath("$.appointments[0].patientName").value("Jane Doe"))
                .andExpect(jsonPath("$.appointments[0].appointments[0].appointment_id").value(mockAppointmentId.toString()));
    }
}
