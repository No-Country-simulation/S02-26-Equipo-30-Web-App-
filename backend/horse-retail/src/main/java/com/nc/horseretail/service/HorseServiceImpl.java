package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.HorseMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseMapper horseMapper;

    @Override
    public void createHorse(HorseRequest request, User owner) {

        log.info("Creating new horse for user {}", owner.getUsername());

        Horse horse = horseMapper.toEntity(request);
        horse.setOwner(owner);

        horseRepository.save(horse);
    }

    @Override
    public List<HorseResponse> getAllHorses() {
        return horseRepository.findAll().stream().map(horseMapper::toDto).toList();
    }

    @Override
    public long countTotalHorses() {
        return horseRepository.count();
    }

    @Override
    public List<HorseResponse> searchHorses(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAllHorses();
        return horseRepository.searchGlobal(keyword).stream()
                .map(horseMapper::toDto).toList();
    }

    @Override
    public HorseResponse getHorseById(UUID id) {
         Horse horse = findByIdOrThrow(id);
         return horseMapper.toDto(horse);
    }

    @Override
    public HorseResponse updateHorse(UUID id, HorseRequest request, User domainUser) {

        Horse horse = findByIdOrThrow(id);
        if (!horse.getOwner().getId().equals(domainUser.getId())) {
            log.warn("User {} attempted to update horse {} owned by another user", domainUser.getUsername(), id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }
        horseMapper.updateEntityFromDto(request, horse);
        Horse savedHorse = horseRepository.save(horse);
        return horseMapper.toDto(savedHorse);
    }

    private Horse findByIdOrThrow(UUID id) {
        return horseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Horse not found with id: " + id));
    }
}
