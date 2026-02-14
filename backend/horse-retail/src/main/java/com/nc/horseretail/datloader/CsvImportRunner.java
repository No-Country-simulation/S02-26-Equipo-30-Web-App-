package com.nc.horseretail.datloader;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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


    @Override
    public void run(ApplicationArguments args) {

        if (!userRepository.existsByEmail("user@test.com")) {
            userRepository.save(
                    User.builder()
                            .fullName("Developer User")
                            .email("user@test.com")
                            .username("dev01")
                            .passwordHash(passwordEncoder.encode("112233"))
                            .role(Role.USER)
                            .build()
            );
        }

        if (horseRepository.count() > 0) {
            return;
        }

        importService.importFromClasspath("data/horsetruth_database.csv");
    }
}