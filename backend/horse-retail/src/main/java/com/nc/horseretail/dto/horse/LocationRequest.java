package com.nc.horseretail.dto.horse;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationRequest {

    @NotBlank
    private String country;
    private String region;
    private String city;
}
