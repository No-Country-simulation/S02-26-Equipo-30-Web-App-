package com.nc.horseretail.service;

import com.nc.horseretail.dto.AuthResponse;
import com.nc.horseretail.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);
}
