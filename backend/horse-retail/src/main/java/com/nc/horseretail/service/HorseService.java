package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;

import java.util.List;

public interface HorseService {

    void createHorse(HorseRequest request);

    List<HorseResponse> getAllHorses();
}
