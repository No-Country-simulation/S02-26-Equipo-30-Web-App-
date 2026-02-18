package com.nc.horseretail.service;

import com.nc.horseretail.dto.ml.MlHorseTrustScoreResponse;
import com.nc.horseretail.model.horse.Horse;

import java.util.Optional;

public interface MlConnectionService {
    Optional<MlHorseTrustScoreResponse> requestTrustScore(Horse horse);
}
