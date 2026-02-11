package com.nc.horseretail.service;

import com.nc.horseretail.dto.ml.MlListingDataResponse;
import com.nc.horseretail.dto.ml.MlRiskResultRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MlIntegrationServiceImpl implements MlIntegrationService {

    @Override
    public MlListingDataResponse getListingDataForMl(UUID listingId) {
        //TODO implement method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<MlListingDataResponse> getAllListingsDataset() {
        //TODO implement method
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void processRiskResult(MlRiskResultRequest request) {
        //TODO implement method
        throw new UnsupportedOperationException("Not implemented yet");
    }
}