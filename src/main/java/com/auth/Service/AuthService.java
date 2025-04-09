package com.auth.Service;

import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Repository.DoctorRepository;
import com.profile.Repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.Dtos.SignupRequest;
import com.auth.Models.user;
import com.auth.Repository.UserRepository;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private  DoctorRepository doctorRepository;
    @Autowired
    private  PatientRepository patientRepository;

    @Autowired
    public AuthService(UserRepository userRepository, DoctorRepository doctorRepository, PatientRepository patientRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // BCrypt encoder instance

    }

    // Method to check if user exists
    public boolean checkUserExists(String email) {
        Optional<user> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent();
    }


    public user registerUser(SignupRequest signupRequest) {
        // Create and save user
        user newUser = new user();
        newUser.setEmail(signupRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setFullName(signupRequest.getFullName());
        newUser.setPhoneNumber(signupRequest.getPhoneNumber());
        newUser.setGender(signupRequest.getGender());

        String role = signupRequest.getRole().toLowerCase();
        if (!role.equals("doctor") && !role.equals("patient")) {
            throw new IllegalArgumentException("Invalid role. Role must be 'doctor' or 'patient'.");
        }
        newUser.setRole(role);
        newUser.setCreatedAt(java.time.Instant.now());

        user savedUser = userRepository.save(newUser);


        // Create Doctor or Patient record based on role
        if ("doctor".equalsIgnoreCase(savedUser.getRole())) {
            Doctor doctor = new Doctor();
            doctor.setDoctorId(savedUser.getId()); // Now ID is available
            doctor.setClinicName(null);
            doctor.setSpeciality(null);
            doctor.setDateOfBirth(null);
            doctor.setProfileCompleted(false);
            doctorRepository.save(doctor);
        } else if ("patient".equalsIgnoreCase(savedUser.getRole())) {
            Patient patient = new Patient();
            patient.setPatientId(savedUser.getId()); // Now ID is available
            patient.setDateOfBirth(null);
            patient.setProfileCompleted(false);
            patientRepository.save(patient);
        }

        return savedUser;
    }




    public user authenticate(String email, String password) {
        user existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Check if user exists in DB
        Optional<user> dbUser = userRepository.findById(existingUser.getId());
        if (dbUser.isEmpty()) {
            throw new RuntimeException("User does not exist in the database!");
        }

        return existingUser;
    }

}
