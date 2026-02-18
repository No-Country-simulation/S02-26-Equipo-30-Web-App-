package com.nc.horseretail.dto.ml;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlHorseTrustScoreRequest {
    private UUID horseId;
    private LocalDate birthDate;
    private String sex;
    private String breed;
    private Double heightM;
    private Double weightKg;
    private Double lengthM;
    private String temperament;
    private String mainUse;
    private String lineage;
    private String birthCountry;
    private String currentCountry;
}
