package com.nc.horseretail.service;

import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsServiceImpl implements MetricsService {

    private final HorseRepository horseRepository;

    @Override
    public long countActiveSellers() {
        return horseRepository.countDistinctOwners();
    }
}
