package com.nc.horseretail.service;

import com.nc.horseretail.config.JwtService;
import com.nc.horseretail.dto.AuthRequest;
import com.nc.horseretail.dto.AuthResponse;
import com.nc.horseretail.dto.RegisterRequest;
import com.nc.horseretail.exception.EmailAlreadyExistException;
import com.nc.horseretail.exception.InvalidCredentialException;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private static final String ROLES = "roles";
    private static final String ROLE_PREFIX = "ROLE_";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Register request received");

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException("This email is already registered. " +
                    "Please log in or use a different one.");
        }

        var user = User.builder()
                .fullName(request.getFullName())
                .username(request.getEmail())
                .createdAt(Instant.now())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN).build();

        userRepository.save(user);

        var claims = buildClaims(user);

        var jwtToken = jwtService.generateToken(claims, user);

        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialException("Invalid email and password");
        }
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var claims = buildClaims(user);

        String jwtToken = jwtService.generateToken(claims, user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder().token(jwtToken).refreshToken(refreshToken).build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refresh Token received");
        String email;

        try {
            email = jwtService.getUserName(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Expired refresh token");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        if (email == null || !jwtService.isTokenValid(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        var user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        var claims = buildClaims(user);

        String newAccessToken = jwtService.generateToken(claims, user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder().token(newAccessToken).refreshToken(newRefreshToken).build();
    }

    private Map<String, Object> buildClaims(User user) {
        return Map.of(ROLES, List.of(ROLE_PREFIX + user.getRole().name()));
    }
}
