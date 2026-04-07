package com.pwms.auth.service;

import com.pwms.auth.dto.*;
import com.pwms.auth.model.User;
import com.pwms.auth.repository.UserRepository;
import com.pwms.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;

    @Value("${jwt.expiration}")
    private long expiration;

    // ── Register ──────────────────────────────────────────────
    public AuthResponseDTO register(RegisterRequestDTO request) {

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new RuntimeException(
                    "Username already exists: " + request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setReferenceId(request.getReferenceId());

        userRepo.save(user);

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getReferenceId()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .referenceId(user.getReferenceId())
                .expiresIn(expiration)
                .build();
    }

    // ── Login ─────────────────────────────────────────────────
    public AuthResponseDTO login(LoginRequestDTO request) {

        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(
                        "Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name(),
                user.getReferenceId()
        );

        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .referenceId(user.getReferenceId())
                .expiresIn(expiration)
                .build();
    }

    // ── Validate — called by Gateway ──────────────────────────
    public boolean validate(String token) {
        return jwtUtil.validateToken(token);
    }

    // ── Extract role — called by Gateway ──────────────────────
    public String extractRole(String token) {
        return jwtUtil.extractRole(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}