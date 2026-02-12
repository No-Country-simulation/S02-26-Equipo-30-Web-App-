package com.nc.horseretail.datloader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorseCsvRow {

    // =========================
    // HORSE
    // =========================
    private String horseId;
    private LocalDate birthDate;
    private String sex;                 // h_sex
    private String breed;               // raza

    private Double heightM;
    private Double weightKg;
    private Double lengthM;

    private Double maxSpeedKmh;

    private String temperament;         // h_temperament
    private String category;            // h_category

    private Integer careerRaces;
    private Double careerTop3Rate;
    private Integer daysSinceLastRace;

    private String lineage;             // h_linaje
    private String birthCountry;        // h_birth_country
    private String currentCountry;      // h_current_country


    // =========================
    // LISTING
    // =========================
    private String listingId;
    private String sellerId;
    private String listingStatus;       // l_listing_status
    private Double askingPriceUsd;
    private Double priceVsMarketRatio;  // l_price_vs_market_ratio
    private Double anomalyScore;        // l_anomaly_score
    private Boolean verifiedListing;
    private LocalDateTime listingCreatedAt;


    // =========================
    // SELLER (USER)
    // =========================
    private String sellerFirstName;     // s_first_name
    private String sellerLastName;      // s_last_name
    private Boolean sellerVerified;     // s_verified
    private Integer sellerDisputes;     // s_disputes
    private Double sellerSuccessRate;   // s_success_rate
    private Integer sellerNumListings;  // s_num_listings
    private Boolean sellerFlaggedFraud; // s_flagged_fraud
    private LocalDateTime sellerCreatedAt;     // s_created_at
    private LocalDateTime sellerLastActiveAt;  // s_last_active_at


    // =========================
    // VET
    // =========================
    private Integer vetTotalExams;
    private Integer vetMajorIssues;
    private Double vetAvgConfidence;
}