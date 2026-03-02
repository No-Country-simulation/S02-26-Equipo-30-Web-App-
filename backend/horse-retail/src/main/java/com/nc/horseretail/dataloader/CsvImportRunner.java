package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class CsvImportRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final HorseRepository horseRepository;
    private final CsvImportService importService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.user.password}")
    private String userPassword;

    private static final String DEV_USER_EMAIL = "user@dev.com";



    @Override
    public void run(ApplicationArguments args) {

        if (!userRepository.existsByEmail(DEV_USER_EMAIL)) {
            userRepository.save(
                    User.builder()
                            .fullName("Dev User")
                            .email(DEV_USER_EMAIL)
                            .username(DEV_USER_EMAIL)
                            .passwordHash(passwordEncoder.encode(userPassword))
                            .emailVerified(true)
                            .role(Role.ADMIN)
                            .build()
            );
        }

        if (horseRepository.count() > 0) {
            return;
        }

        importService.importFromClasspath("data/horsetrust_database.csv");
    }
}