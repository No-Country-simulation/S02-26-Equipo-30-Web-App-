package com.nc.horseretail.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Metric response representing top countries by listings")
public class CountryMetricResponse {

    @Schema(description = "Country name", example = "Germany")
    private String country;

    @Schema(description = "Number of listings in this country", example = "58")
    private Long listingCount;

    @Schema(description = "Percentage of total listings", example = "22.4")
    private Double percentage;
}