package com.auth.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {
    @GetMapping("/api/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(Map.of("message","server is running"));
    }
}
