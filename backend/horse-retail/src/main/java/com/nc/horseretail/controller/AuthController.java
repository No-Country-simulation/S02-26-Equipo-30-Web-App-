package com.nc.horseretail.controller;

import com.nc.horseretail.dto.AuthRequest;
import com.nc.horseretail.dto.AuthResponse;
import com.nc.horseretail.dto.RefreshTokenRequest;
import com.nc.horseretail.dto.RegisterRequest;
import com.nc.horseretail.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ============================
    // REGISTER NEW USER
    // ============================
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Register request received");
        return ResponseEntity.ok(authService.register(request));
    }

    // ============================
    // AUTHENTICATE EXISTING USER
    // ============================
    @Operation(summary = "Login with existing user credentials")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Login request received");
        return ResponseEntity.ok(authService.authenticate(request));
    }

    // ============================
    // REFRESH JWT TOKEN
    // ============================
    @Operation(summary = "Refresh JWT token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid refresh token")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        log.info("🔁 Refresh request received");
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    //TODO POST /forgot-password  → PUBLIC
    //TODO POST /reset-password  → PUBLIC
    //TODO POST /logout  → AUTH
    //TODO GET /me  → AUTH
}
