package com.nc.horseretail.controller;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.dto.metrics.BreedMetricResponse;
import com.nc.horseretail.dto.metrics.CountryMetricResponse;
import com.nc.horseretail.dto.metrics.MonthlyGrowthResponse;
import com.nc.horseretail.service.HorseService;
import com.nc.horseretail.service.MetricsService;
import com.nc.horseretail.config.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Metrics", description = "Platform metrics and statistics API")
public class MetricsController {

    private final MetricsService metricsService;
    private final HorseService horseService;

    // ============================
    // PUBLIC METRICS
    // ============================

    // ============================
    // COUNT SELLERS
    // ============================
    @Operation(
            summary = "Count active sellers",
            description = "Returns the total number of unique active sellers with active listings"
    )
    @ApiResponse(responseCode = "200", description = "Seller count retrieved successfully")
    @GetMapping("/sellers/count")
    public ResponseEntity<Long> getSellersCount() {
        log.info("Request to count active sellers");
        return ResponseEntity.ok(metricsService.countActiveSellers());
    }

    // ============================
    // GET SATISFACTION SCORE
    // ============================
    @Operation(
            summary = "Get satisfaction metrics",
            description = "Returns average rating and total feedback count"
    )
    @ApiResponse(responseCode = "200", description = "Satisfaction metrics retrieved successfully")
    @GetMapping("/satisfaction")
    public ResponseEntity<FeedbackResponse> getSatisfactionScore() {
        log.info("Request to get satisfaction metrics");
        return ResponseEntity.ok(metricsService.getSatisfactionMetrics());
    }

    // ============================
    // COUNT HORSES & LISTINGS
    // ============================
    @Operation(
            summary = "Count all horses",
            description = "Returns the total number of registered horses"
    )
    @ApiResponse(responseCode = "200", description = "Horse count retrieved successfully")
    @GetMapping("/horses/count")
    public ResponseEntity<Long> countHorses() {
        log.info("Request to count all horses");
        return ResponseEntity.ok(horseService.countTotalHorses());
    }

    @Operation(
            summary = "Count all listings",
            description = "Returns total number of listings"
    )
    @ApiResponse(responseCode = "200", description = "Listing count retrieved successfully")
    @GetMapping("/listings/count")
    public ResponseEntity<Long> countListings() {
        log.info("Request to count all listings");
        return ResponseEntity.ok(metricsService.countAllListings());
    }

    // ============================
    // TOP BREEDS & COUNTRIES
    // ============================
    @Operation(
            summary = "Top breeds",
            description = "Returns most popular horse breeds based on listings"
    )
    @ApiResponse(responseCode = "200", description = "Top breeds retrieved successfully")
    @GetMapping("/breeds/top")
    public ResponseEntity<List<BreedMetricResponse>> getTopBreeds() {
        log.info("Request to get top breeds");
        return ResponseEntity.ok(metricsService.getTopBreeds());
    }

    @Operation(
            summary = "Top countries",
            description = "Returns countries with the most listings"
    )
    @ApiResponse(responseCode = "200", description = "Top countries retrieved successfully")
    @GetMapping("/countries/top")
    public ResponseEntity<List<CountryMetricResponse>> getTopCountries() {
        log.info("Request to get top countries");
        return ResponseEntity.ok(metricsService.getTopCountries());
    }

    // ============================
    // FEEDBACK (AUTH)
    // ============================

    @Operation(
            summary = "Submit user feedback",
            description = "Allows authenticated user to submit rating and comment"
    )
    @ApiResponse(responseCode = "201", description = "Feedback submitted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/feedback")
    public ResponseEntity<Void> submitFeedback(
            @Valid @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Feedback submitted by user {}", securityUser.getUsername());

        metricsService.saveFeedback(request, securityUser.getDomainUser());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ============================
    // ADMIN METRICS
    // ============================

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count users (Admin only)")
    @GetMapping("/users/count")
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(metricsService.countUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count conversations (Admin only)")
    @GetMapping("/conversations/count")
    public ResponseEntity<Long> countConversations() {
        return ResponseEntity.ok(metricsService.countConversations());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count sold listings (Admin only)")
    @GetMapping("/listings/sold/count")
    public ResponseEntity<Long> countSoldListings() {
        return ResponseEntity.ok(metricsService.countSoldListings());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Total platform revenue (Admin only)")
    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(metricsService.getTotalRevenue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Monthly growth metrics (Admin only)")
    @GetMapping("/growth/monthly")
    public ResponseEntity<List<MonthlyGrowthResponse>> getMonthlyGrowth() {
        return ResponseEntity.ok(metricsService.getMonthlyGrowth());
    }
}