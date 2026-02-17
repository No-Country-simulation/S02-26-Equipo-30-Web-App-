package com.nc.horseretail.controller;

import com.nc.horseretail.dto.FeedbackRequest;
import com.nc.horseretail.dto.FeedbackResponse;
import com.nc.horseretail.service.MetricsService;
import com.nc.horseretail.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    // ============================
    // COUNT SELLERS
    // ============================
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
    public ResponseEntity<FeedbackResponse> getSatisfactionMetrics() {
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
}