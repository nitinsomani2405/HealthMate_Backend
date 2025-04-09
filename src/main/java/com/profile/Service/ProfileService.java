package com.profile.Service;

import com.auth.Models.user;
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;
import com.profile.Dtos.DoctorProfileResponse;
import com.profile.Dtos.PatientProfileResponse;
import com.profile.Dtos.PostProfileRequest;
import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;

    public Object getProfile(String accessToken) {
        UUID userId = sessionRepository.findUserIdByAccessToken(accessToken.substring(7));

        // Fetch common user info
        user userData = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Return separate response objects based on user role
        if ("doctor".equalsIgnoreCase(userData.getRole())) {
            Doctor doctor = doctorRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor profile not found"));

            return new DoctorProfileResponse(
                    userData.getId(),
                    userData.getEmail(),
                    userData.getFullName(),
                    userData.getPhoneNumber(),
                    userData.getGender(),
                    userData.getRole(),
                    doctor.getDateOfBirth(),
                    doctor.isProfileCompleted(),
                    doctor.getSpeciality(),
                    doctor.getClinicName()
            );
        } else if ("patient".equalsIgnoreCase(userData.getRole())) {
            Patient patient = patientRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));

            return new PatientProfileResponse(
                    userData.getId(),
                    userData.getEmail(),
                    userData.getFullName(),
                    userData.getPhoneNumber(),
                    userData.getGender(),
                    userData.getRole(),
                    patient.getDateOfBirth(),
                    patient.isProfileCompleted()
            );
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user role");
    }


    public ResponseEntity<?> completeProfile(String token, PostProfileRequest request) {
        UUID userId = sessionRepository.findUserIdByAccessToken(token.substring(7));

        user userData = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, String> response = new HashMap<>();

        switch (userData.getRole().toLowerCase()) {
            case "doctor" -> {
                Doctor doctor = doctorRepository.findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor profile not found"));

                doctor.setDateOfBirth(request.getDateOfBirth());
                doctor.setSpeciality(request.getSpeciality());
                doctor.setClinicName(request.getClinicName());
                doctor.setProfileCompleted(true);
                doctorRepository.save(doctor);

                response.put("message", "Doctor profile updated successfully");
            }

            case "patient" -> {
                Patient patient = patientRepository.findById(userId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));

                patient.setDateOfBirth(request.getDateOfBirth());
                patient.setProfileCompleted(true);
                patientRepository.save(patient);

                response.put("message", "Patient profile updated successfully");
            }

            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user role");
        }

        return ResponseEntity.ok(response);
    }

}

