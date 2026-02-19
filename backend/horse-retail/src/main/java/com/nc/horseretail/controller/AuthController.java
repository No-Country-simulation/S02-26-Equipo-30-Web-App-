package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.*;
import com.nc.horseretail.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication and authorization API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // ============================
    // REGISTER NEW USER
    // ============================

    @Operation(summary = "Register new user", description = "Creates a new account and returns JWT tokens")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received for email {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    // ============================
    // LOGIN
    // ============================

    @Operation(summary = "Authenticate user", description = "Authenticates user and returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "Authentication successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login request received for mail {}", request.getEmail());
        return ResponseEntity.ok(authService.authenticate(request));
    }

    // ============================
    // REFRESH TOKEN
    // ============================

    @Operation(summary = "Refresh JWT token", description = "Generates new access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refresh token request received");
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }

    // ============================
    // FORGOT PASSWORD
    // ============================

    @Operation(summary = "Forgot password", description = "Sends password reset link to user email")
    @ApiResponse(responseCode = "200", description = "Reset link sent if email exists")
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    // ============================
    // RESET PASSWORD
    // ============================

    @Operation(summary = "Reset password", description = "Resets password using reset token")
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    // ============================
    // LOGOUT
    // ============================

    @Operation(summary = "Logout user", description = "Invalidates refresh token")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal SecurityUser securityUser) {

        authService.logout(securityUser.getDomainUser());

        return ResponseEntity.ok().build();
    }
}