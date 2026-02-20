package com.nc.horseretail.dto.metrics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Monthly platform growth metrics")
public class MonthlyGrowthResponse {

    @Schema(description = "Year of the metric", example = "2025")
    private Integer year;

    @Schema(description = "Month number (1-12)", example = "3")
    private Integer month;

    @Schema(description = "Total new users registered in the month", example = "45")
    private Long newUsers;

    @Schema(description = "Total new listings created in the month", example = "72")
    private Long newListings;

    @Schema(description = "Total revenue generated in the month", example = "12500.50")
    private BigDecimal revenue;

    @Schema(description = "Percentage growth compared to previous month", example = "8.4")
    private Double growthPercentage;
}