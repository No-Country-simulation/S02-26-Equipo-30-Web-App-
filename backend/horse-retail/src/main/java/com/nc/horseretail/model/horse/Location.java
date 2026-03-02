package com.nc.horseretail.model.horse;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String country;
    private String region;
    private String city;
}