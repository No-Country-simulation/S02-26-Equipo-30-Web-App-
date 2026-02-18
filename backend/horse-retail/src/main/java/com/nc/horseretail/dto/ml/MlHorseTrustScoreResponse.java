package com.nc.horseretail.dto.ml;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MlHorseTrustScoreResponse {
    private Double trustScore;
    private String modelVersion;
    private Instant generatedAt;
}
