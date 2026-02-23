package com.nc.horseretail.repository;

import com.nc.horseretail.model.auth.PasswordResetToken;
import com.nc.horseretail.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByExpiryDateBefore(Instant now);
}