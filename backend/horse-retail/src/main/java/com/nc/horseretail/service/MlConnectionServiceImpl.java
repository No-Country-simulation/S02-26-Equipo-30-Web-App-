package com.nc.horseretail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nc.horseretail.dto.ml.MlHorseTrustScoreRequest;
import com.nc.horseretail.dto.ml.MlHorseTrustScoreResponse;
import com.nc.horseretail.model.horse.Horse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MlConnectionServiceImpl implements MlConnectionService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${ml.enabled:false}")
    private boolean mlEnabled;

    @Value("${ml.base-url:http://localhost:8001}")
    private String mlBaseUrl;

    @Value("${ml.trust-score-path:/predict/trust-score}")
    private String mlTrustScorePath;

    @Override
    public Optional<MlHorseTrustScoreResponse> requestTrustScore(Horse horse) {
        if (!mlEnabled) {
            log.info("ML integration disabled. Skipping trust score request for horse {}", horse.getId());
            return Optional.empty();
        }

        String uri = mlBaseUrl + mlTrustScorePath;
        MlHorseTrustScoreRequest payload = MlHorseTrustScoreRequest.builder()
                .horseId(horse.getId())
                .birthDate(horse.getBirthDate())
                .sex(horse.getSex())
                .breed(horse.getBreed())
                .heightM(horse.getHeightM())
                .weightKg(horse.getWeightKg())
                .lengthM(horse.getLengthM())
                .temperament(horse.getTemperament() != null ? horse.getTemperament().name() : null)
                .mainUse(horse.getMainUse() != null ? horse.getMainUse().name() : null)
                .lineage(horse.getLineage())
                .birthCountry(horse.getBirthCountry())
                .currentCountry(horse.getLocation() != null ? horse.getLocation().getCountry() : null)
                .build();

        try {
            String rawResponse = restClient.post()
                    .uri(uri)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            if (rawResponse == null || rawResponse.isBlank()) {
                log.warn("ML response for horse {} was empty. endpoint={}", horse.getId(), uri);
                return Optional.empty();
            }

            MlHorseTrustScoreResponse response = objectMapper.readValue(rawResponse, MlHorseTrustScoreResponse.class);

            if (response == null || response.getTrustScore() == null) {
                log.warn("ML response for horse {} did not include trustScore. endpoint={}", horse.getId(), uri);
                return Optional.empty();
            }

            if (response.getGeneratedAt() == null) {
                response.setGeneratedAt(Instant.now());
            }

            log.info("ML trust score received for horse {}. score={} modelVersion={}",
                    horse.getId(), response.getTrustScore(), response.getModelVersion());
            return Optional.of(response);
        } catch (Exception ex) {
            log.warn("ML connection failed for horse {}. endpoint={} reason={}",
                    horse.getId(), uri, ex.getMessage());
            return Optional.empty();
        }
    }
}
