package com.nc.horseretail.service;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.horse.TrustScoreStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ArithmeticTrustScoreService {

    private static final String MODEL_VERSION = "arithmetic-v1";

    public void applyTrustScore(Horse horse) {
        double completeness = calculateCompleteness(horse);
        double vetScore = calculateVetScore(horse);
        double sellerScore = calculateSellerScore(horse);

        double trustScore = clamp01(
                0.4 * completeness +
                0.4 * vetScore +
                0.2 * sellerScore
        );

        horse.setTrustScore(round4(trustScore));
        horse.setTrustScoreStatus(TrustScoreStatus.READY);
        horse.setTrustScoreUpdatedAt(Instant.now());
        horse.setTrustModelVersion(MODEL_VERSION);
    }

    private double calculateCompleteness(Horse horse) {
        int totalFields = 11;
        int missing = 0;

        if (isBlank(horse.getSex())) missing++;
        if (isBlank(horse.getBreed())) missing++;
        if (horse.getHeightM() == null) missing++;
        if (horse.getWeightKg() == null) missing++;
        if (horse.getLengthM() == null) missing++;
        if (horse.getMaxSpeedKmh() == null) missing++;
        if (horse.getTemperament() == null) missing++;
        if (horse.getMainUse() == null) missing++;
        if (horse.getCareerRaces() == null) missing++;
        if (horse.getDaysSinceLastRace() == null) missing++;
        if (isBlank(horse.getLineage())) missing++;

        return 1.0 - ((double) missing / totalFields);
    }

    private double calculateVetScore(Horse horse) {
        int totalExams = sanitizePositiveOrZero(horse.getVetTotalExams());
        int majorIssues = sanitizePositiveOrZero(horse.getVetMajorIssues());

        if (totalExams > 0) {
            return clamp01(1.0 - ((double) majorIssues / totalExams));
        }
        return 0.5;
    }

    private double calculateSellerScore(Horse horse) {
        double verified = Boolean.TRUE.equals(horse.getSellerVerified()) ? 1.0 : 0.0;
        double disputes = sanitizePositiveOrZero(horse.getSellerDisputes());
        double flaggedFraud = Boolean.TRUE.equals(horse.getSellerFlaggedFraud()) ? 1.0 : 0.0;

        double disputesComponent = 1.0 - Math.min(disputes / 10.0, 1.0);
        double fraudComponent = 1.0 - flaggedFraud;

        return clamp01(
                0.4 * verified +
                0.3 * disputesComponent +
                0.3 * fraudComponent
        );
    }

    private int sanitizePositiveOrZero(Integer value) {
        if (value == null || value < 0) {
            return 0;
        }
        return value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private double clamp01(double value) {
        if (value < 0.0) return 0.0;
        if (value > 1.0) return 1.0;
        return value;
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
