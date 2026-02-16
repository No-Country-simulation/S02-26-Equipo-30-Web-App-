package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCsvMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(HorseCsvRow row) {


        String externalId = row.getSellerId();

        String fullName = buildFullName(
                row.getSellerFirstName(),
                row.getSellerLastName()
        );

        String username = generateUsername(
                row.getSellerFirstName(),
                row.getSellerLastName(),
                externalId
        );

        String email = generateEmail(username);

        return User.builder()
                .externalId(externalId)
                .username(username)
                .fullName(fullName)
                .email(email)
                .passwordHash(passwordEncoder.encode("123123"))
                .role(Role.USER)
                .emailVerified(Boolean.TRUE.equals(row.getSellerVerified()))
                .accountEnabled(true)
                .lastLoginAt(null)
                .build();
    }

    // =========================
    // Helpers
    // =========================

    private String buildFullName(String first, String last) {
        String f = first == null ? "" : first.trim();
        String l = last == null ? "" : last.trim();
        return (f + " " + l).trim();
    }

    private String generateUsername(String first, String last, String externalId) {

        String base = (first + "." + last)
                .toLowerCase()
                .replaceAll("[^a-z0-9.]", "");

        if (base.isBlank()) {
            base = "user";
        }

        // add externalId to ensure uniqueness
        return base + "_" + externalId.toLowerCase();
    }

    private String generateEmail(String username) {
        return username + "@imported.local";
    }
}