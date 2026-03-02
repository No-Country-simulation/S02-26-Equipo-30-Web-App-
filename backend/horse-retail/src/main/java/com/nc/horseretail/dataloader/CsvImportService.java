package com.nc.horseretail.dataloader;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingVerificationStatus;
import com.nc.horseretail.model.listing.RiskAssessment;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.*;
import com.nc.horseretail.service.ArithmeticTrustScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final CsvParsingService csvParsingService;
    private final HorseCsvMapper horseCsvMapper;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;
    private final UserCsvMapper userCsvMapper;
    private final ListingCsvMapper listingCsvMapper;
    private final ListingVerificationStatusRepository verificationRepository;
    private final RiskAssessmentRepository riskRepository;
    private final ArithmeticTrustScoreService arithmeticTrustScoreService;

    // ======================================================
    // DEV IMPORT (Classpath)
    // ======================================================

    @Transactional
    public void importFromClasspath(String path) {

        ClassPathResource resource = new ClassPathResource(path);

        try (InputStream is = resource.getInputStream()) {

            List<HorseCsvRow> rows = csvParsingService.parse(is);
            processRows(rows);

        } catch (IOException e) {
            throw new IllegalStateException("Failed to import CSV from classpath", e);
        }
    }

    // ======================================================
    // PROD IMPORT (Endpoint Upload)
    // ======================================================

    @Transactional
    public void importFromInputStream(InputStream inputStream) {

        try {

            List<HorseCsvRow> rows = csvParsingService.parse(inputStream);
            processRows(rows);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to import CSV", e);
        }
    }

    // ======================================================
    // COMMON PROCESSING LOGIC
    // ======================================================

    private void processRows(List<HorseCsvRow> rows) {

        int importedCount = 0;

        for (HorseCsvRow row : rows) {

            // Skip if horse already exists
            if (horseRepository.existsByExternalId(row.getHorseId())) {
                continue;
            }

            User user = getOrCreateUser(row);

            Horse horse = horseCsvMapper.toEntity(row, user);
            arithmeticTrustScoreService.applyTrustScore(horse);
            horseRepository.save(horse);

            Listing listing = listingCsvMapper.toEntity(row, horse, user);
            listingRepository.save(listing);

            ListingVerificationStatus verificationStatus =
                    listingCsvMapper.toVerificationStatus(listing, row);
            verificationRepository.save(verificationStatus);

            RiskAssessment riskAssessment =
                    listingCsvMapper.toRiskAssessment(listing, row);
            riskRepository.save(riskAssessment);

            importedCount++;
        }

        log.info("Successfully imported {} new horses ({} total rows processed)",
                importedCount, rows.size());
    }

    // ======================================================
    // HELPER
    // ======================================================

    private User getOrCreateUser(HorseCsvRow row) {

        return userRepository
                .findByExternalId(row.getSellerId())
                .orElseGet(() -> {
                    User newUser = userCsvMapper.toEntity(row);
                    return userRepository.save(newUser);
                });
    }
}