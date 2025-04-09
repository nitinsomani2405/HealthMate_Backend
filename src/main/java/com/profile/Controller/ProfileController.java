package com.profile.Controller;

import com.profile.Dtos.PostProfileRequest;
import com.profile.Service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/get-profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String accessToken) {
        return ResponseEntity.ok(profileService.getProfile(accessToken));
    }

    @PostMapping("/post-profile")
    public ResponseEntity<?> postProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody PostProfileRequest request
    ) {
        return profileService.completeProfile(token, request);
    }



}
