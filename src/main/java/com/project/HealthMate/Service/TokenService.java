package com.project.HealthMate.Service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.project.HealthMate.Repository.UserRepository;  // Your User repository
import com.project.HealthMate.Repository.SessionRepository;  // Your Session repository
import com.project.HealthMate.Models.*;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;  // Inject the session repository

    @Autowired
    public TokenService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    // Method to validate the access token and ensure the user is correct
    public boolean validateAccessToken(String accessToken) {
        var session = sessionRepository.findByAccessToken(accessToken);
        
        // Ensure that the session exists, the access token is valid, and the userId matches
        if (session != null && session.getAccessTokenExpiry().isAfter(Instant.now()) ) {
            return true;  // Token is valid for the correct user
        }

        return false;  // Either the token is invalid, expired, or does not match the user
    }

    // Method to validate the refresh token and ensure the user is correct
    public boolean validateRefreshToken(String refreshToken, UUID userId) {
        var session = sessionRepository.findByRefreshToken(refreshToken);

        // Ensure that the session exists, the refresh token is valid, and the userId matches
        if (session != null && session.getRefreshTokenExpiry().isAfter(Instant.now()) 
                && session.getUser().getId().equals(userId)) {
            return true;  // Token is valid for the correct user
        }
        
        return false;  // Either the token is invalid, expired, or does not match the user
    }

    // Get the userId associated with the access token
    public UUID getUserIdFromAccessToken(String accessToken) {
        // Decode or fetch the user ID associated with the access token
        // This could involve extracting a claim from a JWT token or looking up in a session table
        return sessionRepository.findUserIdByAccessToken(accessToken);
    }

    // Get the userId associated with the refresh token
    public UUID getUserIdFromRefreshToken(String refreshToken) {
        var session = sessionRepository.findByRefreshToken(refreshToken);
        if (session != null) {
            return session.getUser().getId();  // Return the userId associated with the refresh token
        }
        throw new UsernameNotFoundException("User not found for refresh token");
    }

    // This method will load the UserDetails using the email, just like CustomUserDetailService
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user details from the userRepository (using email)
        return userRepository.findByEmail(email)
                .map(UserPrinciple::build)  // Assuming you have a UserPrinciple class to build UserDetails
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    public String generateAccessToken(UUID uuid) {
        return UUID.randomUUID().toString();
    }

    public String generateRefreshToken(UUID uuid) {
        return UUID.randomUUID().toString();
    }
}
