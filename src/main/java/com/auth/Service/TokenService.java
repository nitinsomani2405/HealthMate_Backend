package com.auth.Service;

import java.time.Instant;
import java.util.UUID;

import com.auth.Models.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.Models.*; // Updated here
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;

@Service
public class TokenService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public TokenService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public boolean validateAccessToken(String accessToken) {
        var session = sessionRepository.findByAccessToken(accessToken);
        if (session != null && session.getAccessTokenExpiry().isAfter(Instant.now())) {
            return true;
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken, UUID userId) {
        var session = sessionRepository.findByRefreshToken(refreshToken);

        if (session != null && session.getRefreshTokenExpiry().isAfter(Instant.now())
                && session.getUser().getId().equals(userId)) {
            return true;
        }
        return false;
    }

    public UUID getUserIdFromAccessToken(String accessToken) {
        return sessionRepository.findUserIdByAccessToken(accessToken);
    }

    public UUID getUserIdFromRefreshToken(String refreshToken) {
        var session = sessionRepository.findByRefreshToken(refreshToken);
        if (session != null) {
            return session.getUser().getId();
        }
        throw new UsernameNotFoundException("User not found for refresh token");
    }

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(UserPrinciple::build)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public String generateAccessToken(UUID uuid) {
        return UUID.randomUUID().toString();
    }

    public String generateRefreshToken(UUID uuid) {
        return UUID.randomUUID().toString();
    }
}