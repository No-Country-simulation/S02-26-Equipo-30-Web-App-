package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.mapper.HorseMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.security.currentuser.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final CurrentUserProvider currentUserProvider;
    private final HorseMapper horseMapper;

    @Override
    public void createHorse(HorseRequest request) {

        log.info("Creating new horse");

        //TODO: replace with user from auth
        User owner = currentUserProvider.getCurrentUser();

        Horse horse = horseMapper.toEntity(request);
        horse.setOwner(owner);

        horseRepository.save(horse);
    }

    @Override
    public List<HorseResponse> getAllHorses() {
        return horseRepository.findAll().stream().map(horseMapper::toDto).toList();
    }
}
