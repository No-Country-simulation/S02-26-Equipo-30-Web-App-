package com.nc.horseretail.dto.horse;

import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.horse.Temperament;
import lombok.*;

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
    private Temperament temperament;
    private MainUse mainUse;
    private String lineage;
    private String birthCountry;
    private LocationResponse location;
}
