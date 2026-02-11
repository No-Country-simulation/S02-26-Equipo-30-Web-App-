package com.nc.horseretail.datloader;

import com.nc.horseretail.model.enums.RiskLevel;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class ListingCsvMapper {

    public Listing toEntity(HorseCsvRow row, Horse horse) {

        return Listing.builder()
                .externalId(row.getListingId())
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

        if (value == null) return ListingStatus.DRAFT;

        String normalized = value.toLowerCase();

        if (normalized.contains("active") || normalized.contains("public")) {
            return ListingStatus.PUBLIC;
        }

        if (normalized.contains("under")) {
            return ListingStatus.UNDER_REVIEW;
        }

        if (normalized.contains("sold") || normalized.contains("closed")) {
            return ListingStatus.CLOSED;
        }

        return ListingStatus.DRAFT;
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

        // 🚨 FRAUDE
        if (Boolean.TRUE.equals(row.getSellerFlaggedFraud())) {
            return RiskLevel.HIGH;
        }

        // 🚨 Anomaly score alto
        if (row.getAnomalyScore() != null && row.getAnomalyScore() > 0.7) {
            return RiskLevel.HIGH;
        }

        // ⚠️ Problemas veterinarios
        if (row.getVetMajorIssues() != null && row.getVetMajorIssues() > 0) {
            return RiskLevel.MEDIUM;
        }

        // ⚠️ Precio muy inflado
        if (row.getPriceVsMarketRatio() != null && row.getPriceVsMarketRatio() > 1.5) {
            return RiskLevel.MEDIUM;
        }

        // ⚠️ Baja tasa de éxito
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