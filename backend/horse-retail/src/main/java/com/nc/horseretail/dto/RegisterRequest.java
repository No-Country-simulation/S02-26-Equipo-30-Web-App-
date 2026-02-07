package com.nc.horseretail.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String name;
    private String lastname;
    private String email;
    private String password;
}
