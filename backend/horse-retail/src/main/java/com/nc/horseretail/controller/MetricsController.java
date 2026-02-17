package com.nc.horseretail.controller;

import com.nc.horseretail.service.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
