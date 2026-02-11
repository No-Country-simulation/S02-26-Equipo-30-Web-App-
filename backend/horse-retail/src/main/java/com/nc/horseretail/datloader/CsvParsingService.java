package com.nc.horseretail.datloader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParsingService {

    public List<HorseCsvRow> parse(InputStream csvInputStream) {

        List<HorseCsvRow> rows = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(csvInputStream, StandardCharsets.UTF_8)
                );
                CSVParser csvParser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim()
                        .parse(reader)
        ) {

            for (CSVRecord csvRecord : csvParser) {

                HorseCsvRow row = new HorseCsvRow();

                row.setHorseId(csvRecord.get("horse_id"));
                row.setBirthDate(LocalDate.parse(csvRecord.get("birth_date")));
                row.setSex(csvRecord.get("h_sex"));
                row.setBreed(csvRecord.get("raza"));

                row.setHeightM(parseDouble(csvRecord.get("height_m")));
                row.setWeightKg(parseDouble(csvRecord.get("weight_kg")));
                row.setLengthM(parseDouble(csvRecord.get("length_m")));
                row.setMaxSpeedKmh(parseDouble(csvRecord.get("max_speed_kmh")));

                row.setTemperament(csvRecord.get("h_temperament"));
                row.setCategory(csvRecord.get("h_category"));

                //TODO parse career and days since last to double
                row.setCareerRaces(parseInt(csvRecord.get("h_career_races")));
                row.setCareerTop3Rate(parseDouble(csvRecord.get("h_career_top3_rate")));
                row.setDaysSinceLastRace(parseInt(csvRecord.get("h_days_since_last_race")));

                row.setLineage(csvRecord.get("h_linaje"));
                row.setBirthCountry(csvRecord.get("h_birth_country"));
                row.setCurrentCountry(csvRecord.get("h_current_country"));

                row.setListingId(csvRecord.get("listing_id"));
                row.setSellerId(csvRecord.get("seller_id"));
                row.setListingStatus(csvRecord.get("l_listing_status"));
                row.setAskingPriceUsd(parseDouble(csvRecord.get("l_asking_price_usd")));

                row.setVerifiedListing(Boolean.parseBoolean(csvRecord.get("l_verified_listing")));
                row.setListingCreatedAt(
                        LocalDateTime.parse(
                                csvRecord.get("l_created_at").replace(" ", "T")
                        )
                );

                rows.add(row);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Error reading CSV file", e);
        }

        return rows;
    }

    // ======================
    // Helpers
    // ======================

    private Double parseDouble(String value) {
        return (value == null || value.isBlank()) ? null : Double.valueOf(value);
    }

    private Integer parseInt(String value) {
        if (value == null || value.isBlank()) return null;

        // TODO valor truncado, revisar si es correcto o si se debería redondear
        double d = Double.parseDouble(value);
        return (int) d;
    }
}