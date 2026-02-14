package com.nc.horseretail.service;

import com.nc.horseretail.dto.AuthRequest;
import com.nc.horseretail.dto.AuthResponse;
import com.nc.horseretail.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(AuthRequest request);

    AuthResponse refreshToken(String refreshToken);
}
