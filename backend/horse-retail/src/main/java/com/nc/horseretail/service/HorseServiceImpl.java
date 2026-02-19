package com.nc.horseretail.service;

import com.nc.horseretail.dto.HorseRequest;
import com.nc.horseretail.dto.HorseResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.HorseMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseMapper horseMapper;

    // ============================
    // CREATE HORSE
    // ============================
    @Override
    public void createHorse(HorseRequest request, User owner) {

        log.info("Creating new horse for user {}", owner.getUsername());

        Horse horse = horseMapper.toEntity(request);
        horse.setOwner(owner);

        horseRepository.save(horse);
    }

    // ============================
    // GET / SEARCH HORSES (PAGINATED)
    // ============================
    @Override
    public Page<HorseResponse> getHorses(String keyword, Pageable pageable) {

        Page<Horse> horsePage;

        if (keyword == null || keyword.isBlank()) {
            horsePage = horseRepository.findAll(pageable);
        } else {
            horsePage = horseRepository.search(keyword.trim(), pageable);
        }

        return horsePage.map(horseMapper::toDto);
    }

    // ============================
    // GET HORSE BY ID
    // ============================
    @Override
    public HorseResponse getHorseById(UUID id) {
        Horse horse = findByIdOrThrow(id);
        return horseMapper.toDto(horse);
    }

    // ============================
    // UPDATE HORSE
    // ============================
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

    // ============================
    // DELETE HORSE
    // ============================
    @Override
    public void deleteHorse(UUID id, User domainUser) {
        Horse horse = findByIdOrThrow(id);
        if (!horse.getOwner().getId().equals(domainUser.getId())) {
            log.warn("User {} attempted to delete horse {} owned by another user", domainUser.getUsername(), id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }
        //TODO: Implement soft delete
        throw new BusinessException("Soft delete not implemented yet");
    }

    // ============================
    // GET MY HORSES
    // ============================
    @Override
    public List<HorseResponse> getMyHorses(User domainUser) {
        return horseRepository.findAllByOwner(domainUser);
    }

    // ============================
    // COUNT MY HORSES
    // ============================
    @Override
    public Long countMyHorses(User domainUser) {
        return horseRepository.countHorsesByOwner(domainUser);
    }

    // ============================
    // COUNT HORSES
    // ============================
    @Override
    public long countTotalHorses() {
        return horseRepository.count();
    }


    @Override
    public void deleteHorseByAdmin(UUID horseId) {
//TODO implement method
        throw new BusinessException("Method not implemented yet");
    }


    // ============================
    // HELPER METHODS
    // ============================

    private Horse findByIdOrThrow(UUID id) {
        return horseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Horse not found with id: " + id));
    }
}
