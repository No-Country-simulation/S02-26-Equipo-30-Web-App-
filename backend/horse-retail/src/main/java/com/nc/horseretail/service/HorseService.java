package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.user.User;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface HorseService {

    void createHorse(HorseRequest request, User owner);

    List<HorseResponse> getAllHorses();

    long countTotalHorses();
    
    List<HorseResponse> searchHorses(String keyword);

    HorseResponse getHorseById(UUID id);

    HorseResponse updateHorse(UUID id, @Valid HorseRequest request, User domainUser);
}
