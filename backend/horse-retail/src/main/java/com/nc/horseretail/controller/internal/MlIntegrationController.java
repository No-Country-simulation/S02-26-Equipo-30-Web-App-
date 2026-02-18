package com.nc.horseretail.controller.internal;

import com.nc.horseretail.dto.ml.MlListingDataResponse;
import com.nc.horseretail.dto.ml.MlRiskResultRequest;
import com.nc.horseretail.service.MlIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internal/ml")
@RequiredArgsConstructor
public class MlIntegrationController {

    private final MlIntegrationService mlIntegrationService;


    //TODO ML gets listing data for analysis (features + historical data)
    @GetMapping("/listings/{listingId}")
    public ResponseEntity<MlListingDataResponse> getListingForAnalysis(@PathVariable UUID listingId) {
        return ResponseEntity.ok(mlIntegrationService.getListingDataForMl(listingId));
    }

    //TODO ML gets all listings data for training (features + historical data + labels)
    @GetMapping("/dataset/listings")
    public ResponseEntity<List<MlListingDataResponse>> getListingsDataset() {
        return ResponseEntity.ok(mlIntegrationService.getAllListingsDataset());
    }

    //TODO ML sends risk analysis results for a listing (listingId + riskScore + riskFactors)
    @PostMapping("/results")
    public ResponseEntity<Void> receiveRiskResult(@RequestBody MlRiskResultRequest request) {
        mlIntegrationService.processRiskResult(request);
        return ResponseEntity.ok().build();
    }
    //TODO POST /predict-price
    //TODO POST /recommendations
    //TODO GET /recommendations/me
    //TODO POST /analyze-horse
    //TODO POST /fraud-detection
}