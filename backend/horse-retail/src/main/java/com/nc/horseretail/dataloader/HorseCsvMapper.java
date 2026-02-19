package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.horse.Location;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.horse.Temperament;
import com.nc.horseretail.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.nc.horseretail.model.horse.MainUse.*;
import static com.nc.horseretail.model.horse.Temperament.*;

@Component
@RequiredArgsConstructor
public class HorseCsvMapper {

    public Horse toEntity(HorseCsvRow row, User owner) {



        return Horse.builder()
                .externalId(row.getHorseId())
                .name("Horse " + row.getHorseId())
                .owner(owner)
                .birthDate(row.getBirthDate())
                .sex(row.getSex())
                .breed(row.getBreed())
                .heightM(row.getHeightM())
                .weightKg(row.getWeightKg())
                .lengthM(row.getLengthM())
                .temperament(mapTemperament(row.getTemperament()))
                .mainUse(mapMainUse(row.getCategory()))
                .lineage(row.getLineage())
                .birthCountry(row.getBirthCountry())
                .location(
                        Location.builder()
                                .country(row.getCurrentCountry())
                                .build()
                )
                .build();
    }

    // ======================
    // Helpers
    // ======================

    private Temperament mapTemperament(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.toLowerCase();

        if (normalized.contains("manso") || normalized.contains("calm")) {
            return CALM;
        }

        if (normalized.contains("dispuesto") || normalized.contains("balanced")) {
            return BALANCED;
        }

        if (normalized.contains("hot") || normalized.contains("nerv")) {
            return HOT;
        }

        if (normalized.contains("agres")) {
            return AGGRESSIVE;
        }

        // fallback seguro
        return BALANCED;
    }

    private MainUse mapMainUse(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.toLowerCase();

        if (normalized.contains("race") || normalized.contains("carrera")) {
            return RACING;
        }

        if (normalized.contains("jump") || normalized.contains("salto")) {
            return SHOW_JUMPING;
        }

        if (normalized.contains("dressage") || normalized.contains("doma")) {
            return DRESSAGE;
        }

        if (normalized.contains("breed") || normalized.contains("cría")) {
            return BREEDING;
        }

        return RECREATIONAL;
    }
}