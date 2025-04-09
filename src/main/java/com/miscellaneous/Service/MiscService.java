package com.miscellaneous.Service;

import com.appointment.Repository.AppointmentRepository;
import com.auth.Models.user;
import com.auth.Repository.UserRepository;
import com.miscellaneous.Dtos.ConnectedPatientDTO;
import com.miscellaneous.Dtos.DoctorResponseDTO;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MiscService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    //FETCH ALL REGISTERED DOCTORS
    public List<DoctorResponseDTO> getAllDoctors() {
        List<user> users = userRepository.findAllDoctors();
        List<UUID> userIds = users.stream().map(user::getId).toList();
        List<Doctor> doctorDetails = doctorRepository.findByUserIds(userIds);

        Map<UUID, Doctor> doctorMap = doctorDetails.stream()
                .collect(Collectors.toMap(d -> d.getUser().getId(), Function.identity()));

        return users.stream().map(user -> {
            Doctor doc = doctorMap.get(user.getId());
            return new DoctorResponseDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getGender(),
                    doc != null ? doc.getSpeciality() : null,
                    doc != null ? doc.getClinicName() : null
            );
        }).collect(Collectors.toList());
    }

    //FETCH ALL PATIENTS APPOINTED BY PATIENT
    public List<ConnectedPatientDTO> getPatientsConnectedToDoctor(UUID doctorId) {
        List<Patient> patients = appointmentRepository.findDistinctPatientsByDoctorId(doctorId);

        return patients.stream()
                .map(patient -> {
                    user user = patient.getUser();
                    return new ConnectedPatientDTO(
                            patient.getPatientId(),
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhoneNumber(),
                            user.getGender(),
                            patient.getDateOfBirth()
                    );
                })
                .collect(Collectors.toList());
    }

}
