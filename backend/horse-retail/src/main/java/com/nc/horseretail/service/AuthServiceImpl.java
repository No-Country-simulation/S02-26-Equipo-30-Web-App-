package com.nc.horseretail.service;

import com.nc.horseretail.dto.AuthResponse;
import com.nc.horseretail.dto.RegisterRequest;
import com.nc.horseretail.exception.EmailAlreadyExistException;
import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {

        log.info("Register request received");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException("This email is already registered. Please log in or use a different one.");
        }

        User user = User.builder()
                .username(generateUniqueUsername(request.getFullName()))
                .fullName(request.getFullName()).email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        //TODO generate token and return it in response
        return AuthResponse.builder().build();
    }


    //============
    //HELPERS
    //============

    private String generateUniqueUsername(String fullName) {

        String base = fullName.toLowerCase().replaceAll("\\s+", "_").replaceAll("[^a-z0-9_]", "");

        String username = base;
        int suffix = 1;

        while (userRepository.existsByUsername(username)) {
            username = base + "_" + suffix;
            suffix++;
        }

        return username;
    }
}
