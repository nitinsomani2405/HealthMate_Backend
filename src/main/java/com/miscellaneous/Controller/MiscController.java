package com.miscellaneous.Controller;

import com.auth.Repository.SessionRepository;
import com.miscellaneous.Dtos.ConnectedPatientDTO;
import com.miscellaneous.Dtos.DoctorResponseDTO;
import com.miscellaneous.Dtos.GetAllDoctorsResponseDTO;
import com.miscellaneous.Dtos.GetConnectedPatientsResponseDTO;
import com.miscellaneous.Service.MiscService;
import com.profile.Repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MiscController {

    private final MiscService miscService;
    private final SessionRepository sessionRepository;
    private final PatientRepository patientRepository;

    //API FOR FETCHING ALL REGISTERED DOCTOR
    @GetMapping("/doctors")
    public ResponseEntity<GetAllDoctorsResponseDTO> getAllDoctors(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        UUID userId = sessionRepository.findUserIdByAccessToken(token);

        patientRepository.findByPatientId(userId)
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        List<DoctorResponseDTO> doctors = miscService.getAllDoctors();
        return ResponseEntity.ok(new GetAllDoctorsResponseDTO(doctors));
    }

    //API TO FETCH ALL PATIENTS  APPOINTED BY DOCTOR
    @GetMapping("/doctor/patients")
    public ResponseEntity<GetConnectedPatientsResponseDTO> getConnectedPatients(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        UUID doctorId = sessionRepository.findUserIdByAccessToken(token);

        List<ConnectedPatientDTO> patients = miscService.getPatientsConnectedToDoctor(doctorId);

        return ResponseEntity.ok(new GetConnectedPatientsResponseDTO(patients));
    }

}
