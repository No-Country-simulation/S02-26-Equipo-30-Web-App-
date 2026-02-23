package com.nc.horseretail.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Metric response representing top horse breeds")
public class BreedMetricResponse {

    @Schema(description = "Breed name", example = "Arabian")
    private String breed;

    @Schema(description = "Number of listings for this breed", example = "42")
    private Long listingCount;

    @Schema(description = "Percentage of total listings", example = "18.5")
    private Double percentage; // TODO calculate percentage based on total listings count
}
