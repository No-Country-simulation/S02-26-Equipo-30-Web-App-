package com.nc.horseretail.service;

import com.nc.horseretail.dto.horse.HorseRequest;
import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.model.horse.MainUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HorseService {

    void createHorse(HorseRequest request, UUID userId);

    long countTotalHorses();

    HorseResponse getHorseById(UUID id);

    HorseResponse updateHorse(UUID id, HorseRequest request, UUID userId);

    void deleteHorse(UUID id, UUID userId);

    List<HorseResponse> getMyHorses(UUID userId);

    Long countMyHorses(UUID userId);

    Page<HorseResponse> getHorses(String keyword, MainUse mainUse, Pageable pageable);

    void deleteHorseByAdmin(UUID horseId);
}
