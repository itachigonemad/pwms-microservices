package com.pwms.auth.controller;

import com.pwms.auth.dto.*;
import com.pwms.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /auth/register
    // Body: { "username":"ashok", "password":"123", "role":"PATIENT", "referenceId":1 }
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // POST /auth/login
    // Body: { "username":"ashok", "password":"123" }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // GET /auth/validate?token=eyJhbGci...
    // Called internally by API Gateway to validate token
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        return ResponseEntity.ok(authService.validate(token));
    }

    // GET /auth/role?token=eyJhbGci...
    // Called by Gateway to extract role for authorization
    @GetMapping("/role")
    public ResponseEntity<String> getRole(@RequestParam String token) {
        return ResponseEntity.ok(authService.extractRole(token));
    }
}