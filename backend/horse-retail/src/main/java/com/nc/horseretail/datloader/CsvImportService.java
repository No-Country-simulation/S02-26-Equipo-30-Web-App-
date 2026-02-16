package com.nc.horseretail.datloader;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingVerificationStatus;
import com.nc.horseretail.model.listing.RiskAssessment;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.*;
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

    @Transactional
    public void importFromClasspath(String path) {

        ClassPathResource resource = new ClassPathResource(path);

        try (InputStream is = resource.getInputStream()) {

            List<HorseCsvRow> rows = csvParsingService.parse(is);

            for (HorseCsvRow row : rows) {

                // Skip if horse already exists
                if (horseRepository.existsByExternalId(row.getHorseId())) {
                    continue;
                }

                // Get or create User
                User user = userRepository
                        .findByExternalId(row.getSellerId())
                        .orElseGet(() -> {
                            User newUser = userCsvMapper.toEntity(row);
                            return userRepository.save(newUser);
                        });

                // Create Horse
                Horse horse = horseCsvMapper.toEntity(row, user);
                horseRepository.save(horse);

                // Create Listing
                Listing listing = listingCsvMapper.toEntity(row, horse, user);
                listingRepository.save(listing);

                // Verification
                ListingVerificationStatus verificationStatus =
                        listingCsvMapper.toVerificationStatus(listing, row);

                verificationRepository.save(verificationStatus);

                // Risk
                RiskAssessment riskAssessment =
                        listingCsvMapper.toRiskAssessment(listing, row);

                riskRepository.save(riskAssessment);

                //TODO  create vets
            }

            log.info("Successfully imported {} horses", rows.size());

        } catch (IOException e) {
            throw new IllegalStateException("Failed to import CSV", e);
        }
    }
}