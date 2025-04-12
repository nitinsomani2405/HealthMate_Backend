package com.auth.Controller;

import com.auth.Models.Session;
import com.auth.Repository.SessionRepository;
import com.auth.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.auth.Dtos.DataDTO;
import com.auth.Dtos.ErrorResponseDTO;
import com.auth.Dtos.LoginRequest;
import com.auth.Dtos.LoginResponseDTO;
import com.auth.Dtos.RefreshTokenRequestDTO;
import com.auth.Dtos.RefreshTokenResponseDTO;
import com.auth.Dtos.SessionDTO;
import com.auth.Dtos.SessionDataDTO;
import com.auth.Dtos.SignupRequest;
import com.auth.Dtos.SignupResponseDTO;
import com.auth.Dtos.UserDTO;
import com.auth.Dtos.UserDataDTO;
import com.auth.Models.user;
import com.auth.Service.AuthService;
import com.auth.Service.SessionService;
import com.auth.Service.TokenService;

import jakarta.validation.Valid;

import java.time.Instant;
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
    private final SessionRepository sessionRepository;

    public AuthController(AuthService authService, TokenService tokenService, SessionService sessionService, UserRepository userRepository, SessionRepository sessionRepository) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
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
            

            Session session =sessionService.createSession(authenticatedUser.getId(), accessToken, refreshToken);
    	    // Retrieve session details (assuming session is created after successful authentication)

 
            DataDTO dataDTO = new DataDTO(
                    new UserDTO(authenticatedUser.getId(), authenticatedUser.getEmail(), authenticatedUser.getCreatedAt()),
                    new SessionDTO(accessToken, refreshToken,session.getAccessTokenExpiry().toString(), session.getRefreshTokenExpiry().toString())
            );

            return ResponseEntity.ok(new LoginResponseDTO(dataDTO, authenticatedUser.getRole()));
    	} catch (Exception e) {
    	    return ResponseEntity.badRequest().body(new ErrorResponseDTO("Login Error", "Invalid credentials"));
    	}

    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        try {
            String refreshtoken= refreshTokenRequest.getRefreshToken();
            Session s= sessionRepository.findByRefreshToken(refreshtoken);
            if(s==null){
                ResponseEntity.badRequest().body(new ErrorResponseDTO("Refresh Token", "Invalid refresh token"));
            }
            String oldaccesstokenexpiry=s.getAccessTokenExpiry().toString();
            String oldrefreshtokenexpiry=s.getRefreshTokenExpiry().toString();
            String newAccessToken = sessionService.refreshAccessToken(refreshTokenRequest.getRefreshToken());

            return ResponseEntity.ok(new RefreshTokenResponseDTO(new SessionDataDTO(new SessionDTO(newAccessToken, refreshTokenRequest.getRefreshToken(),oldaccesstokenexpiry,oldrefreshtokenexpiry))));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO("Refresh Token Error", e.getMessage()));
        }
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