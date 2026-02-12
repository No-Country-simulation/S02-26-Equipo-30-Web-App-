package com.nc.horseretail.service;

import com.nc.horseretail.dto.ml.MlListingDataResponse;
import com.nc.horseretail.dto.ml.MlRiskResultRequest;

import java.util.List;
import java.util.UUID;

public interface MlIntegrationService {

    MlListingDataResponse getListingDataForMl(UUID listingId);

    List<MlListingDataResponse> getAllListingsDataset();

    void processRiskResult(MlRiskResultRequest request);
}