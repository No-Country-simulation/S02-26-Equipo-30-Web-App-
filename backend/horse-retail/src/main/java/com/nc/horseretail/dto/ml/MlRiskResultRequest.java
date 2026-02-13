package com.nc.horseretail.dto.ml;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlRiskResultRequest {

    private UUID listingId;

    private Double anomalyScore;
    private Double priceVsMarketRatio;

    private String riskLevel; // LOW, MEDIUM, HIGH
    private String primaryRiskFactor;

    private Instant generatedAt;
}