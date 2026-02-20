package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.user.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HorseService {

    void createHorse(HorseRequest request, User owner);

    long countTotalHorses();

    HorseResponse getHorseById(UUID id);

    HorseResponse updateHorse(UUID id, @Valid HorseRequest request, User domainUser);

    void deleteHorse(UUID id, User domainUser);

    List<HorseResponse> getMyHorses(User domainUser);

    Long countMyHorses(User domainUser);

    Page<HorseResponse> getHorses(String keyword, MainUse mainUse, Pageable pageable);
}
