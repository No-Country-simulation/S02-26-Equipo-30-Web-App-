package com.nc.horseretail.service;

import com.nc.horseretail.dto.horse.HorseRequest;
import com.nc.horseretail.dto.horse.HorseResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.HorseMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.horse.HorseStatus;
import com.nc.horseretail.model.horse.MainUse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class HorseServiceImpl implements HorseService {

    private final HorseRepository horseRepository;
    private final HorseMapper horseMapper;
    private final ArithmeticTrustScoreService arithmeticTrustScoreService;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    // ============================
    // CREATE HORSE
    // ============================
    @Override
    public void createHorse(HorseRequest request, UUID userId) {

        User owner = getUserOrThrow(userId);

        log.info("Creating new horse for user {}", owner.getUsername());

        Horse horse = horseMapper.toEntity(request);
        horse.setOwner(owner);
        horse.setStatus(HorseStatus.ACTIVE);
        if (horse.getSellerVerified() == null) {
            horse.setSellerVerified(owner.isEmailVerified());
        }
        arithmeticTrustScoreService.applyTrustScore(horse);
        horseRepository.save(horse);
    }

    // ============================
    // GET / SEARCH HORSES (PAGINATED)
    // ============================
    @Override
    public Page<HorseResponse> getHorses(String keyword, MainUse mainUse, Pageable pageable) {
        String sanitizedKeyword = keyword == null ? "" : keyword.trim();
        if (sanitizedKeyword.isBlank()) {
            sanitizedKeyword = "";
        }
        return horseRepository.search(sanitizedKeyword, mainUse, pageable).map(horseMapper::toDto);
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
    public HorseResponse updateHorse(UUID id, HorseRequest request, UUID userId) {

        Horse horse = findByIdOrThrow(id);
        if (!horse.getOwner().getId().equals(userId)) {
            log.warn("User attempted to update horse {} owned by another user", id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }
        horseMapper.updateEntityFromDto(request, horse);
        arithmeticTrustScoreService.applyTrustScore(horse);
        Horse savedHorse = horseRepository.save(horse);
        return horseMapper.toDto(savedHorse);
    }

    // ============================
    // DELETE HORSE
    // ============================
    @Transactional
    @Override
    public void deleteHorse(UUID id, UUID userId) {

        Horse horse = findByIdOrThrow(id);

        if (!horse.getOwner().getId().equals(userId)) {
            log.warn("User {} attempted to delete horse {} owned by another user", userId, id);
            throw new ResourceNotFoundException("Horse not found with id: " + id);
        }

        if (horse.getStatus() == HorseStatus.DELETED) {
            throw new BusinessException("Horse already deleted");
        }

        List<Listing> activeListings =
                listingRepository.findByHorseIdAndStatus(id, ListingStatus.ACTIVE);

        activeListings.forEach(listing ->
                listing.setStatus(ListingStatus.CANCELLED)
        );

        horse.setStatus(HorseStatus.DELETED);

        log.info("Horse {} soft deleted by owner {}", id, userId);
    }

    // ============================
    // GET MY HORSES
    // ============================
    @Override
    public List<HorseResponse> getMyHorses(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return horseRepository.findAllByOwner(user)
                .stream()
                .map(horseMapper::toDto)
                .toList();
    }

    // ============================
    // COUNT MY HORSES
    // ============================
    @Override
    public Long countMyHorses(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return horseRepository.countHorsesByOwner(user);
    }

    // ============================
    // COUNT HORSES
    // ============================
    @Override
    public long countTotalHorses() {
        return horseRepository.count();
    }


    @Transactional
    @Override
    public void deleteHorseByAdmin(UUID horseId) {

        Horse horse = horseRepository.findById(horseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horse not found with id: " + horseId));

        if (horse.getStatus() == HorseStatus.DELETED) {
            throw new BusinessException("Horse is already deleted");
        }

        List<Listing> activeListings =
                listingRepository.findByHorseIdAndStatus(horseId, ListingStatus.ACTIVE);

        for (Listing listing : activeListings) {
            listing.setStatus(ListingStatus.CANCELLED);
        }

        horse.setStatus(HorseStatus.DELETED);

        horseRepository.save(horse);
    }


    // ============================
    // HELPER METHODS
    // ============================

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Horse findByIdOrThrow(UUID id) {
        return horseRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Horse not found with id: " + id));
    }
}
