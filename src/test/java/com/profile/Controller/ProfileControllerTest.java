package com.profile.Controller;

import com.profile.Models.Doctor;
import com.profile.Models.Patient;
import com.profile.Service.ProfileService;
import com.profile.Dtos.PostProfileRequest;
import com.profile.Dtos.DoctorProfileResponse;
import com.profile.Dtos.PatientProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    private UUID mockUserId;

    @BeforeEach
    void setUp() {
        mockUserId = UUID.randomUUID();
    }

    @Test
    void getProfile_ValidId_ReturnsDoctorProfile() throws Exception {
        DoctorProfileResponse mockResponse = new DoctorProfileResponse();
        mockResponse.setId(mockUserId);
        mockResponse.setFullName("John Doe");
        mockResponse.setEmail("john@example.com");
        mockResponse.setPhoneNumber("1234567890");
        mockResponse.setGender("Male");
        mockResponse.setRole("doctor");
        mockResponse.setDateOfBirth("1990-01-01");
        mockResponse.setProfileCompleted(true);
        mockResponse.setSpeciality("Cardiology");
        mockResponse.setClinicName("Heart Care");
        
        when(profileService.getProfile(mockUserId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/get-profile/" + mockUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("doctor"))
                .andExpect(jsonPath("$.speciality").value("Cardiology"));
    }

    @Test
    void getProfile_ValidId_ReturnsPatientProfile() throws Exception {
        PatientProfileResponse mockResponse = new PatientProfileResponse();
        mockResponse.setId(mockUserId);
        mockResponse.setFullName("Jane Doe");
        mockResponse.setEmail("jane@example.com");
        mockResponse.setPhoneNumber("9876543210");
        mockResponse.setGender("Female");
        mockResponse.setRole("patient");
        mockResponse.setDateOfBirth("1995-01-01");
        mockResponse.setProfileCompleted(true);
        
        when(profileService.getProfile(mockUserId)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/get-profile/" + mockUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserId.toString()))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.role").value("patient"));
    }

    @Test
    void postProfile_ValidData_ReturnsSuccess() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Profile updated successfully");
        
        @SuppressWarnings("rawtypes")
        ResponseEntity responseEntity = ResponseEntity.ok(response);
        when(profileService.completeProfile(any(String.class), any(PostProfileRequest.class)))
                .thenReturn(responseEntity);

        mockMvc.perform(post("/api/post-profile")
                .header("Authorization", "Bearer valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"John Doe\",\"email\":\"john@example.com\"," +
                        "\"phone\":\"1234567890\",\"gender\":\"Male\",\"dateOfBirth\":\"1990-01-01\"," +
                        "\"speciality\":\"Cardiology\",\"clinicName\":\"Heart Care\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile updated successfully"));
    }
} 