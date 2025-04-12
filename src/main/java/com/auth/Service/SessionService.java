package com.auth.Service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.auth.Models.Session;
import com.auth.Models.user;
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class SessionService {
	private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TokenService tokenService;

    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    public SessionService(SessionRepository sessionRepository, TokenService tokenService,UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.tokenService = tokenService;
        this.userRepository=userRepository;
    }
    
    @Transactional
    public Session createSession(UUID userId, String accessToken, String refreshToken) {
        Instant now = Instant.now();

        // ✅ Fetch user from DB to ensure it's managed by Hibernate
        user existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // ✅ Create session using a properly managed user
        Session session = new Session(
                existingUser, // Ensure user is managed
                accessToken,
                refreshToken,
                now,
                now.plus(ACCESS_TOKEN_EXPIRATION),
                now.plus(REFRESH_TOKEN_EXPIRATION)
        );

        try {
            sessionRepository.save(session);
        } catch (Exception e) {
            e.printStackTrace(); // Log full error
            throw new RuntimeException("Error while saving session: " + e.getMessage());
        }
        return session;
    }

    public String refreshAccessToken(String refreshToken) {
        Session session = sessionRepository.findByRefreshToken(refreshToken);

        // Check if refresh token has expired
        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) {
            sessionRepository.delete(session); // Logout user by deleting the session
            throw new RuntimeException("Refresh token expired. Please log in again.");
        }

        // Generate a new access token if refresh token is valid
        String newAccessToken = tokenService.generateAccessToken(session.getUser().getId());

        // Update session with new access token and new access token expiry time
        session.setAccessToken(newAccessToken);
        session.setAccessTokenExpiry(Instant.now().plus(ACCESS_TOKEN_EXPIRATION));
        sessionRepository.save(session);

        return newAccessToken;
    }
}
