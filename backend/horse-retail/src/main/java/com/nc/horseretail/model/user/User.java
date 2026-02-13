package com.nc.horseretail.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    // =========================
    // Public identity
    // =========================

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // =========================
    // Private identity
    // =========================

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // =========================
    // App role
    // =========================

    @Enumerated(EnumType.STRING)
    @Column(name = "app_role", nullable = false)
    private Role role;

    // =========================
    // Status
    // =========================

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "account_enabled", nullable = false)
    private boolean accountEnabled;

    // =========================
    // Auditing
    // =========================

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        this.accountEnabled = true;
        this.emailVerified = false;
        if (this.role == null) {
            this.role = Role.USER;
        }
    }
}