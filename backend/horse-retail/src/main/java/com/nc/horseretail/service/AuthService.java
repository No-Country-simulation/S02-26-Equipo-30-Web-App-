package com.nc.horseretail.service;

import com.nc.horseretail.dto.auth.AuthRequest;
import com.nc.horseretail.dto.auth.AuthResponse;
import com.nc.horseretail.dto.auth.RegisterRequest;
import com.nc.horseretail.dto.auth.ResetPasswordRequest;
import com.nc.horseretail.dto.auth.VerifyEmailRequest;

import java.util.UUID;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(String refreshToken);

    AuthResponse verifyEmail(VerifyEmailRequest request);

    void resetPassword(ResetPasswordRequest request);

    void forgotPassword( String email);

    void logout(UUID userId);
}
