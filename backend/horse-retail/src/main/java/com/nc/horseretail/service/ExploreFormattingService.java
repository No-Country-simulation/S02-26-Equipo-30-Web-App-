package com.nc.horseretail.service;

import com.nc.horseretail.model.horse.Location;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ExploreFormattingService {

    public Integer calculateAgeYears(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String formatEnumLabel(Enum<?> value) {
        if (value == null) {
            return null;
        }

        String[] tokens = value.name().toLowerCase(Locale.ROOT).split("_");
        List<String> words = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            if (token.isBlank()) {
                continue;
            }
            words.add(Character.toUpperCase(token.charAt(0)) + token.substring(1));
        }
        return String.join(" ", words);
    }

    public String formatLocationLabel(Location location) {
        if (location == null) {
            return null;
        }

        List<String> parts = new ArrayList<>(3);
        if (location.getCity() != null && !location.getCity().isBlank()) {
            parts.add(location.getCity());
        }
        if (location.getRegion() != null && !location.getRegion().isBlank()) {
            parts.add(location.getRegion());
        }
        if (parts.isEmpty() && location.getCountry() != null && !location.getCountry().isBlank()) {
            parts.add(location.getCountry());
        }

        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    public Double normalizeTrustScore(Double trustScore) {
        if (trustScore == null) {
            return null;
        }

        double normalized = trustScore <= 1 ? trustScore * 100 : trustScore;
        return Math.round(normalized * 10.0) / 10.0;
    }

    public String formatTrustLabel(Double trustScore) {
        Double normalizedTrustScore = normalizeTrustScore(trustScore);
        if (normalizedTrustScore == null) {
            return null;
        }
        if (normalizedTrustScore >= 90) {
            return "Excellent";
        }
        if (normalizedTrustScore >= 75) {
            return "Good";
        }
        if (normalizedTrustScore >= 60) {
            return "Fair";
        }
        return "Needs Review";
    }
}
