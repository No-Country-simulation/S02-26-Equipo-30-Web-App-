package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.enums.RiskLevel;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.*;
import com.nc.horseretail.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ListingCsvMapper {

    public Listing toEntity(HorseCsvRow row, Horse horse, User user) {

        return Listing.builder()
                .externalId(row.getListingId())
                .owner(user)
                .horse(horse)
                .askingPriceUsd(row.getAskingPriceUsd())
                .status(mapStatus(row.getListingStatus()))
                .createdAt(mapCreatedAt(row.getListingCreatedAt()))
                .build();
    }

    public ListingVerificationStatus toVerificationStatus(
            Listing listing,
            HorseCsvRow row
    ) {

        return ListingVerificationStatus.builder()
                .listing(listing)
                .overallStatus(mapVerification(row.getVerifiedListing()))
                .lastUpdated(Instant.now())
                .build();
    }

    public RiskAssessment toRiskAssessment(
            Listing listing,
            HorseCsvRow row
    ) {

        RiskLevel riskLevel = calculateRiskLevel(row);
        String primaryFactor = determinePrimaryRiskFactor(row);

        return RiskAssessment.builder()
                .listing(listing)
                .riskLevel(riskLevel)
                .primaryRiskFactor(primaryFactor)
                .generatedAt(Instant.now())
                .build();
    }

    // =========================
    // Helpers
    // =========================

    private ListingStatus mapStatus(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return ListingStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid listing status: " + value);
        }
    }

    private OverallVerificationStatus mapVerification(Boolean verified) {

        if (Boolean.TRUE.equals(verified)) {
            return OverallVerificationStatus.VERIFIED;
        }

        return OverallVerificationStatus.UNVERIFIED;
    }

    private Instant mapCreatedAt(LocalDateTime value) {

        if (value == null) {
            return Instant.now();
        }

        return value.atZone(ZoneId.systemDefault()).toInstant();
    }

    private RiskLevel calculateRiskLevel(HorseCsvRow row) {

        // FRAUD
        if (Boolean.TRUE.equals(row.getSellerFlaggedFraud())) {
            return RiskLevel.HIGH;
        }

        // High anomaly score
        if (row.getAnomalyScore() != null && row.getAnomalyScore() > 0.7) {
            return RiskLevel.HIGH;
        }

        // Vet problems
        if (row.getVetMajorIssues() != null && row.getVetMajorIssues() > 0) {
            return RiskLevel.MEDIUM;
        }

        // Overpriced
        if (row.getPriceVsMarketRatio() != null && row.getPriceVsMarketRatio() > 1.5) {
            return RiskLevel.MEDIUM;
        }

        // Low success rate
        if (row.getSellerSuccessRate() != null && row.getSellerSuccessRate() < 0.5) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }

    private String determinePrimaryRiskFactor(HorseCsvRow row) {

        if (Boolean.TRUE.equals(row.getSellerFlaggedFraud())) {
            return "Seller flagged for fraud";
        }

        if (row.getAnomalyScore() != null && row.getAnomalyScore() > 0.7) {
            return "High anomaly score from ML model";
        }

        if (row.getVetMajorIssues() != null && row.getVetMajorIssues() > 0) {
            return "Veterinary major issues detected";
        }

        if (row.getPriceVsMarketRatio() != null && row.getPriceVsMarketRatio() > 1.5) {
            return "Price significantly above market";
        }

        if (row.getSellerSuccessRate() != null && row.getSellerSuccessRate() < 0.5) {
            return "Low seller success rate";
        }

        return "No significant risk factors";
    }
}