package com.project.HealthMate.Controller;

import com.project.HealthMate.Service.AuthService;
import com.project.HealthMate.Service.SessionService;
import com.project.HealthMate.Service.TokenService;

import com.project.HealthMate.Dtos.SignupResponseDTO;
import com.project.HealthMate.Dtos.UserDTO;
import com.project.HealthMate.Dtos.UserDataDTO;
import com.project.HealthMate.Dtos.SignupRequest;
import com.project.HealthMate.Dtos.DataDTO;

import com.project.HealthMate.Dtos.ErrorResponseDTO;
import com.project.HealthMate.Dtos.LoginRequest;

import com.project.HealthMate.Dtos.LoginResponseDTO;
import com.project.HealthMate.Dtos.RefreshTokenRequestDTO;
import com.project.HealthMate.Dtos.RefreshTokenResponseDTO;
import com.project.HealthMate.Dtos.SessionDTO;
import com.project.HealthMate.Dtos.SessionDataDTO;
import com.project.HealthMate.Models.user;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/auth") // Base route for authentication
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final SessionService sessionService;

    public AuthController(AuthService authService, TokenService tokenService, SessionService sessionService) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
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
    	   

//    	    // Create UserDTO
//    	    UserDTO userDTO = new UserDTO(
//    	        authenticatedUser.getId(),
//    	        authenticatedUser.getEmail(),
//    	        authenticatedUser.getCreatedAt()
//    	    );

 
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
        	System.out.println("in try");
            String newAccessToken = sessionService.refreshAccessToken(refreshTokenRequest.getRefreshToken());

            return ResponseEntity.ok(new RefreshTokenResponseDTO(new SessionDataDTO(new SessionDTO(newAccessToken, refreshTokenRequest.getRefreshToken()))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Refresh Token Error", e.getMessage()));
        }
    }
    
    @GetMapping("/hello")
    public String Hello() {
    	return "hello";
    }
    
    @GetMapping("/bye")
    public String bye() {
    	return "bye";
    }


}