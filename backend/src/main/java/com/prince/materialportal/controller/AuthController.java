package com.prince.materialportal.controller;

import com.prince.materialportal.dto.*;
import com.prince.materialportal.service.AuthService;
import com.prince.materialportal.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login, registration, and session info")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates student or faculty with username and password, returns JWT token.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Login successful", response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new student or faculty account.")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        UserDto user = authService.register(request);
        return ResponseEntity.status(201).body(ResponseBuilder.buildCreated("User registered successfully", user));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ResponseBuilder.buildError("Not authenticated", 401));
        }
        UserDto user = authService.getCurrentUser(principal.getName());
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("User details retrieved", user));
    }

    @GetMapping("/status")
    @Operation(summary = "Check authentication service status")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAuthStatus() {
        return ResponseEntity.ok(ResponseBuilder.buildSuccess("Auth service is running", Map.of("status", "UP")));
    }
}
