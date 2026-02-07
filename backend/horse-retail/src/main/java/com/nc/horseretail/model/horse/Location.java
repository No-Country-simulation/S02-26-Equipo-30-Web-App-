package com.nc.horseretail.model.horse;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private String country;
    private String region;
    private String city;
}