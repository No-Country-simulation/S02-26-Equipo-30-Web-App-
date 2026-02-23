package com.nc.horseretail.dto.auth;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthResponse {

    private String token;
    private String refreshToken;
}