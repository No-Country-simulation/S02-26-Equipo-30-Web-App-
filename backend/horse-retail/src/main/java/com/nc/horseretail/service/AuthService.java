package com.nc.horseretail.service;

import com.nc.horseretail.dto.auth.AuthRequest;
import com.nc.horseretail.dto.auth.AuthResponse;
import com.nc.horseretail.dto.auth.RegisterRequest;
import com.nc.horseretail.dto.auth.ResetPasswordRequest;
import com.nc.horseretail.model.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(String refreshToken);

    void resetPassword(ResetPasswordRequest request);

    void forgotPassword(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 150, message = "Email cannot exceed 150 characters") String email);

    void logout(User domainUser);
}
