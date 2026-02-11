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


    //    ML obtiene datos de un listing para analisis
    @GetMapping("/listings/{listingId}")
    public ResponseEntity<MlListingDataResponse> getListingForAnalysis(@PathVariable UUID listingId) {
        return ResponseEntity.ok(mlIntegrationService.getListingDataForMl(listingId));
    }

    //    ML obtiene dataset completo de listings para entrenar modelo
    @GetMapping("/dataset/listings")
    public ResponseEntity<List<MlListingDataResponse>> getListingsDataset() {
        return ResponseEntity.ok(mlIntegrationService.getAllListingsDataset());
    }

    //     ML envia resultado de analisis de riesgo para un listing
    @PostMapping("/results")
    public ResponseEntity<Void> receiveRiskResult(@RequestBody MlRiskResultRequest request) {
        mlIntegrationService.processRiskResult(request);
        return ResponseEntity.ok().build();
    }
}