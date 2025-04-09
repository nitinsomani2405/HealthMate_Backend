package com.appointment.Service;

import com.appointment.Dtos.*;
import com.appointment.Models.Appointment;
import com.appointment.Repository.AppointmentRepository;
import com.auth.Repository.SessionRepository;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final SessionRepository sessionRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;



    //BOOK APPOINTMENT
    public BookAppointmentResponseDTO bookAppointment(String token, BookAppointmentRequestDTO request) {
        UUID patientId = sessionRepository.findUserIdByAccessToken(token.substring(7));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(Appointment.Status.PENDING);

        Appointment saved = appointmentRepository.save(appointment);

        return new BookAppointmentResponseDTO(
                "Appointment booked successfully",
                saved.getAppointmentId(),
                saved.getStatus()
        );
    }

    //CANCEL APPOINTMENT
    public BookAppointmentResponseDTO cancelAppointment(String token, UUID appointmentId) {
        UUID userId = sessionRepository.findUserIdByAccessToken(token.substring(7));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));


        boolean isPatient = appointment.getPatient().getUser().getId().equals(userId);
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(userId);

        if (!isPatient && !isDoctor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to cancel this appointment");
        }

        appointment.setStatus(Appointment.Status.CANCELLED);
        appointmentRepository.save(appointment);

        return new BookAppointmentResponseDTO(
                "Appointment cancelled successfully",
                appointment.getAppointmentId(),
                appointment.getStatus()
        );
    }

    //CONFIRM APPOINTMENT
    public BookAppointmentResponseDTO confirmAppointment(String token, UUID appointmentId) {
        UUID doctorId = sessionRepository.findUserIdByAccessToken(token.substring(7));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (!appointment.getDoctor().getUser().getId().equals(doctorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only assigned doctor can confirm the appointment");
        }
        if (appointment.getStatus() == Appointment.Status.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot confirm cancelled appointment");
        }
        appointment.setStatus(Appointment.Status.CONFIRMED);
        appointmentRepository.save(appointment);

        return new BookAppointmentResponseDTO(
                "Appointment confirmed successfully",
                appointment.getAppointmentId(),
                appointment.getStatus()
        );
    }

    //ALL APPOINTMENTS FOR PATIENT GROUP BY DOCTOR
    public List<DoctorAppointmentGroupDTO> getAppointmentsForPatient(String token) {
        UUID patientId = sessionRepository.findUserIdByAccessToken(token.substring(7));


        List<Appointment> appointments = appointmentRepository.findByPatientUserId(patientId);

        // Group by doctor
        Map<UUID, List<Appointment>> grouped = appointments.stream()
                .collect(Collectors.groupingBy(a -> a.getDoctor().getUser().getId()));

        List<DoctorAppointmentGroupDTO> result = new ArrayList<>();

        for (Map.Entry<UUID, List<Appointment>> entry : grouped.entrySet()) {
            UUID doctorId = entry.getKey();
            List<Appointment> doctorAppointments = entry.getValue();
            String doctorName = doctorAppointments.get(0).getDoctor().getUser().getFullName();

            List<SingleAppointmentDTO> appointmentList = doctorAppointments.stream()
                    .map(a -> new SingleAppointmentDTO(
                            a.getAppointmentId(),
                            a.getAppointmentDate(),
                            a.getAppointmentTime(),
                            a.getStatus().toString(),
                            a.getReason()
                    )).collect(Collectors.toList());

            result.add(new DoctorAppointmentGroupDTO(doctorId, doctorName, appointmentList));
        }

        return result;
    }

    //ALL APPOINTMENTS FOR DOCTOR GROUP BY PATIENT
    public List<PatientAppointmentGroupDTO> getAppointmentsGroupedByPatient(String token) {
        UUID doctorId = sessionRepository.findUserIdByAccessToken(token.substring(7));

        List<Appointment> appointments = appointmentRepository.findByDoctorUserId(doctorId);

        Map<UUID, List<Appointment>> groupedByPatient = appointments.stream()
                .collect(Collectors.groupingBy(app -> app.getPatient().getUser().getId()));

        return groupedByPatient.entrySet().stream()
                .map(entry -> {
                    UUID patientId = entry.getKey();
                    List<Appointment> patientAppointments = entry.getValue();
                    String patientName = patientAppointments.get(0).getPatient().getUser().getFullName();

                    List<SingleAppointmentDTO> appointmentDTOs = patientAppointments.stream()
                            .map(app -> new SingleAppointmentDTO(
                                    app.getAppointmentId(),
                                    app.getAppointmentDate(),
                                    app.getAppointmentTime(),
                                    app.getStatus().name(), // assuming enum
                                    app.getReason()
                            ))
                            .collect(Collectors.toList());

                    return new PatientAppointmentGroupDTO(patientId, patientName, appointmentDTOs);
                })
                .collect(Collectors.toList());
    }

}
