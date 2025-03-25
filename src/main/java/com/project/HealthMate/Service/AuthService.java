package com.project.HealthMate.Service;

import com.project.HealthMate.Models.user;
import com.project.HealthMate.Repository.UserRepository;
import com.project.HealthMate.Dtos.SignupRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // BCrypt encoder instance
    }

    // Method to check if user exists
    public boolean checkUserExists(String email) {
        Optional<user> existingUser = userRepository.findByEmail(email);
        return existingUser.isPresent();
    }

    // Method to register user, with encrypted password
    public user registerUser(SignupRequest signupRequest) {
        user newUser = new user();
        newUser.setEmail(signupRequest.getEmail());

        // Encode password before saving to the database
        newUser.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        newUser.setFullName(signupRequest.getFullName());
        newUser.setPhoneNumber(signupRequest.getPhoneNumber());
        newUser.setGender(signupRequest.getGender());
        newUser.setRole(signupRequest.getRole());
        newUser.setCreatedAt(java.time.Instant.now());

        return userRepository.save(newUser);
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
