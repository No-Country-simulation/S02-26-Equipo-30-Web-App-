package com.nc.horseretail.dto;

import com.nc.horseretail.model.user.Role;
import com.nc.horseretail.model.user.UserStatus;
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

    private UserStatus status;

    private Instant lastLoginAt;

}
