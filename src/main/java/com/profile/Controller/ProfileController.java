package com.profile.Controller;

import com.profile.Dtos.PostProfileRequest;
import com.profile.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get-profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PostMapping("/post-profile")
    public ResponseEntity<?> postProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody PostProfileRequest request
    ) {
        return profileService.completeProfile(token, request);
    }



}
