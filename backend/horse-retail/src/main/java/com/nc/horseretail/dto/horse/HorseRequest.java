package com.nc.horseretail.dto.horse;

import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.horse.Temperament;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseRequest {

    @NotBlank
    private String name;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String sex;
    @NotBlank
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

    @Valid
    @NotNull
    private LocationRequest location;

    private Boolean sellerVerified;
    private Integer sellerDisputes;
    private Boolean sellerFlaggedFraud;

    private Integer vetTotalExams;
    private Integer vetMajorIssues;
}
