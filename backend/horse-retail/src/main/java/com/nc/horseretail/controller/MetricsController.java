package com.nc.horseretail.controller;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.service.HorseService;
import com.nc.horseretail.service.MetricsService;
import com.nc.horseretail.config.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
@Slf4j
public class MetricsController {

    private final MetricsService metricsService;
    private final HorseService horseService;

    // ============================
    // COUNT SELLERS
    // ============================
    //TODO GET /sellers/count  → PUBLIC
    @Operation(summary = "Count active sellers", description = "Returns the total number of unique active sellers with listings")
    @ApiResponse(responseCode = "200", description = "Seller count retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/sellers/count")
    public ResponseEntity<Long> getSellersCount() {
        log.info("Received request to count active sellers");
        return ResponseEntity.ok(metricsService.countActiveSellers());
    }

    // ============================
    // SATISFACTION METRICS
    // ============================
    @Operation(summary = "Get satisfaction metrics", description = "Returns the average rating and total number of feedbacks")
    @ApiResponse(responseCode = "200", description = "Satisfaction metrics retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/satisfaction")
    public ResponseEntity<FeedbackResponse> getSatisfactionScore() {
        log.info("Received request to get satisfaction metrics");
        return ResponseEntity.ok(metricsService.getSatisfactionMetrics());
    }

    // ============================
    // SUBMIT FEEDBACK
    // ============================
    @Operation(summary = "Submit user feedback", description = "Allows an authenticated user to submit a rating and comment")
    @ApiResponse(responseCode = "200", description = "Feedback submitted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/feedback")
    public ResponseEntity<String> submitFeedback(
            @RequestBody FeedbackRequest request,
            @AuthenticationPrincipal SecurityUser securityUser) {
        
        log.info("Received feedback request from user: {}", securityUser.getUsername());
        metricsService.saveFeedback(request, securityUser.getDomainUser()); 
        
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    // ============================
    // COUNT HORSES
    // ============================
    //TODO GET /horses/count  → PUBLIC
    @Operation(summary = "Count all horses", description = "Returns the total number of horses available")
    @ApiResponse(responseCode = "200", description = "Horse count retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/count")
    public ResponseEntity<Long> countHorses() {
        log.info("Received request to count all horses");
        return ResponseEntity.ok(horseService.countTotalHorses());
    }

    //TODO GET /listings/count  → PUBLIC
    //TODO GET /breeds/top  → PUBLIC
    //TODO GET /countries/top  → PUBLIC
    //TODO GET /users/count  → ADMIN ONLY
    //TODO GET /conversations/count  → ADMIN ONLY
    //TODO GET /listings/sold/count  → ADMIN ONLY
    //TODO GET /revenue/total  → ADMIN ONLY
    //TODO GET /growth/monthly  → ADMIN ONLY
}
