package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.model.user.UserStatus;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {

        validateConfiguration();

        // Keep the production admin in sync with configured credentials on every startup.
        User admin = userRepository.findByEmail(adminEmail)
                .map(this::refreshAdminAccount)
                .orElseGet(this::buildAdminAccount);

        userRepository.save(admin);
        log.info("Production admin user synchronized: {}", adminEmail);
    }

    private void validateConfiguration() {
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException("Property 'app.admin.email' must be configured in production.");
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("Property 'app.admin.password' must be configured in production.");
        }

        if (adminPassword.length() < 8) {
            throw new IllegalStateException("Admin password must contain at least 8 characters.");
        }
    }

    private User buildAdminAccount() {
        return User.builder()
                .email(adminEmail)
                .username(adminEmail)
                .fullName("System Admin")
                .passwordHash(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .emailVerified(true)
                .accountEnabled(true)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private User refreshAdminAccount(User admin) {
        admin.setUsername(adminEmail);
        admin.setFullName("System Admin");
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEmailVerified(true);
        admin.setAccountEnabled(true);
        admin.setStatus(UserStatus.ACTIVE);
        return admin;
    }
}
