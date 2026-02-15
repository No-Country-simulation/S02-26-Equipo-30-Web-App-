package com.nc.horseretail.dto.ml;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlListingDataResponse {

    private UUID listingId;
    private UUID horseId;

    // Horse data
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

    // Career
    private Integer careerRaces;
    private Double careerTop3Rate;
    private Integer daysSinceLastRace;

    // Listing
    private Double askingPriceUsd;
    private Instant createdAt;
}