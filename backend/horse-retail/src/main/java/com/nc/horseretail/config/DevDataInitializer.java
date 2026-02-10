package com.nc.horseretail.config;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataInitializer {

    private final UserRepository userRepository;

    @PostConstruct
    void init() {
        if (!userRepository.existsByEmail("dev@horseretail.com")) {
            userRepository.save(
                    User.builder()
                            .email("dev@horseretail.com")
                            .username("dev_user")
                            .fullName("Developer User")
                            .passwordHash("noop")
                            .role(Role.USER)
                            .build()
            );
        }
    }
}