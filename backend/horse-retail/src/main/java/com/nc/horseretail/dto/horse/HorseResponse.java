package com.nc.horseretail.dto.horse;

import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.horse.Temperament;
import com.nc.horseretail.model.horse.TrustScoreStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseResponse {

    private String name;
    private LocalDate birthDate;
    private String sex;
    private String breed;
    private Double heightM;
    private Double weightKg;
    private Double lengthM;
    private Double maxSpeedKmh;
    private Temperament temperament;
    private MainUse mainUse;
    private String lineage;
    private Integer careerRaces;
    private Integer daysSinceLastRace;
    private String birthCountry;
    private LocationResponse location;
    private Boolean sellerVerified;
    private Integer sellerDisputes;
    private Boolean sellerFlaggedFraud;
    private Integer vetTotalExams;
    private Integer vetMajorIssues;
    private Double trustScore;
    private TrustScoreStatus trustScoreStatus;
    private Instant trustScoreUpdatedAt;
    private String trustModelVersion;
}
