package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.model.user.User;

import java.util.List;

public interface HorseService {

    void createHorse(HorseRequest request, User owner);

    List<HorseResponse> getAllHorses();

    long countTotalHorses(); // Método independiente
    
    List<HorseResponse> searchHorses(String keyword);
}
