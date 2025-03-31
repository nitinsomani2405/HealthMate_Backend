package com.auth.project.HealthMate.Controller;

import com.auth.project.HealthMate.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.auth.project.HealthMate.Dtos.DataDTO;
import com.auth.project.HealthMate.Dtos.ErrorResponseDTO;
import com.auth.project.HealthMate.Dtos.LoginRequest;
import com.auth.project.HealthMate.Dtos.LoginResponseDTO;
import com.auth.project.HealthMate.Dtos.RefreshTokenRequestDTO;
import com.auth.project.HealthMate.Dtos.RefreshTokenResponseDTO;
import com.auth.project.HealthMate.Dtos.SessionDTO;
import com.auth.project.HealthMate.Dtos.SessionDataDTO;
import com.auth.project.HealthMate.Dtos.SignupRequest;
import com.auth.project.HealthMate.Dtos.SignupResponseDTO;
import com.auth.project.HealthMate.Dtos.UserDTO;
import com.auth.project.HealthMate.Dtos.UserDataDTO;
import com.auth.project.HealthMate.Models.user;
import com.auth.project.HealthMate.Service.AuthService;
import com.auth.project.HealthMate.Service.SessionService;
import com.auth.project.HealthMate.Service.TokenService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // Base route for authentication
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, TokenService tokenService, SessionService sessionService, UserRepository userRepository) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        // If validation fails, return validation errors
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

            // Create the ErrorResponseDTO with the appropriate error and message
            ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                "Signup Error",  // error type
                String.join(", ", errors) // formatted error message
            );

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Check if user already exists
        if (authService.checkUserExists(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Signup Error", "User already exists"));
        }

        // Register new user
        user newUser = authService.registerUser(signupRequest);

        // Prepare UserDTO
        UserDTO userDTO = new UserDTO(
            newUser.getId(),
            newUser.getEmail(),
            newUser.getCreatedAt()
        );

        // Wrap in UserDataDTO
        UserDataDTO userDataDTO = new UserDataDTO(userDTO);

        // Wrap in SignupResponseDTO
        SignupResponseDTO response = new SignupResponseDTO(userDataDTO);

        return ResponseEntity.ok(response);
    }


    // Login endpoint with validation handling
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        // If validation fails, return validation errors
    	if (bindingResult.hasErrors()) {
    	    List<String> errors = bindingResult.getFieldErrors()
    	        .stream()
    	        .map(FieldError::getDefaultMessage)
    	        .collect(Collectors.toList());

    	    // Create the ErrorResponseDTO with the appropriate error and message
    	    ErrorResponseDTO errorResponse = new ErrorResponseDTO(
    	        "Login Error", // error type
    	        String.join(", ", errors) // formatted error message
    	    );

    	    return ResponseEntity.badRequest().body(errorResponse);
    	}

    	try {
    	    // Authenticate user
    	    user authenticatedUser = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
    	    
    	    String accessToken = tokenService.generateAccessToken(authenticatedUser.getId());
            String refreshToken = tokenService.generateRefreshToken(authenticatedUser.getId());
            

            sessionService.createSession(authenticatedUser.getId(), accessToken, refreshToken);
    	    // Retrieve session details (assuming session is created after successful authentication)

 
            DataDTO dataDTO = new DataDTO(
                    new UserDTO(authenticatedUser.getId(), authenticatedUser.getEmail(), authenticatedUser.getCreatedAt()),
                    new SessionDTO(accessToken, refreshToken)
            );

            return ResponseEntity.ok(new LoginResponseDTO(dataDTO, authenticatedUser.getRole()));
    	} catch (Exception e) {
    	    return ResponseEntity.badRequest().body(new ErrorResponseDTO("Login Error", "Invalid credentials"));
    	}

    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        try {
            String newAccessToken = sessionService.refreshAccessToken(refreshTokenRequest.getRefreshToken());

            return ResponseEntity.ok(new RefreshTokenResponseDTO(new SessionDataDTO(new SessionDTO(newAccessToken, refreshTokenRequest.getRefreshToken()))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Refresh Token Error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("message","server is running"));
    }

    @GetMapping("/user-role")
    public ResponseEntity<?> userRole(@RequestHeader("Authorization") String accessToken) {
        accessToken=accessToken.substring(7);
        if(!tokenService.validateAccessToken(accessToken)){
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Access Token Error", "Invalid Access Token"));
        }
        UUID userId = tokenService.getUserIdFromAccessToken(accessToken);
        user u=userRepository.findById(userId).get();
        return ResponseEntity.ok(Map.of("role", u.getRole()));

    }


}