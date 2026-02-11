package com.nc.horseretail.datloader;

import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class CsvImportRunner implements ApplicationRunner {

    private final HorseRepository horseRepository;
    private final CsvImportService importService;

    @Override
    public void run(ApplicationArguments args) {

        if (horseRepository.count() > 0) {
            return;
        }

        importService.importFromClasspath("data/horsetruth_database.csv");
    }
}