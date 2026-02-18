package com.nc.horseretail.dto;

import com.nc.horseretail.model.user.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserResponse {

    private UUID id;

    private String externalId;

    private String username;

    private String fullName;

    private String email;

    private Role role;

    private boolean emailVerified;

    private boolean accountEnabled;

    private Instant createdAt;

    private Instant lastLoginAt;

}
