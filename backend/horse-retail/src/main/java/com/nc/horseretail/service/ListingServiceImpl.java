package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingFilterRequest;
import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ForbiddenOperationException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.ListingMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingSpecification;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingServiceImpl implements ListingService {

    private final ListingMapper listingMapper;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;
    private final UserRepository userRepository;

    private static final String LISTING_NOT_FOUND = "Listing not found with id: %s";

    // ============================
    // CREATE
    // ============================

    @Override
    public ListingResponse createListing(ListingRequest dto, UUID securityUserId) {

        Horse horse = horseRepository.findById(dto.getHorseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horse not found with id: " + dto.getHorseId()));

        if (!horse.getOwner().getId().equals(securityUserId)) {
            throw new ForbiddenOperationException("You are not allowed to perform this operation");
        }

        if (listingRepository.existsByHorseIdAndStatus(horse.getId(), ListingStatus.ACTIVE)) {
            throw new BusinessException("Horse already has an active listing");
        }

        Listing listing = listingMapper.toEntity(dto);

        listing.setOwner(horse.getOwner());
        listing.setHorse(horse);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setExternalId(UUID.randomUUID().toString());

        return listingMapper.toDto(listingRepository.save(listing));
    }

    // ============================
    // PUBLIC LISTINGS
    // ============================

    @Override
    public Page<ListingResponse> getListings(String keyword, Pageable pageable) {

        Page<Listing> page;

        if (keyword == null || keyword.isBlank()) {
            page = listingRepository.findByStatus(ListingStatus.ACTIVE, pageable);
        } else {
            page = listingRepository.searchActive(keyword.trim(), pageable);
        }

        return page.map(listingMapper::toDto);
    }

    @Override
    public ListingResponse getListingById(UUID id) {

        Listing listing = findListingOrThrow(id);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new ResourceNotFoundException(LISTING_NOT_FOUND.formatted(id));
        }

        return listingMapper.toDto(listing);
    }

    @Override
    public Page<ListingResponse> searchListings(
            ListingFilterRequest filter,
            Pageable pageable) {

        Specification<Listing> spec = ListingSpecification.withFilters(filter);

        return listingRepository.findAll(spec, pageable)
                .map(listingMapper::toDto);
    }

    // ============================
    // UPDATE
    // ============================

    @Override
    public ListingResponse updateListing(UUID id, ListingRequest dto, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() == ListingStatus.SOLD ||
                listing.getStatus() == ListingStatus.CANCELLED) {
            throw new BusinessException("Cannot update a finalized listing");
        }

        listing.setAskingPriceUsd(dto.getPrice());

        return listingMapper.toDto(listingRepository.save(listing));
    }

    // ============================
    // DELETE
    // ============================

    @Override
    public void deleteListing(UUID id, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new BusinessException("Cannot delete a sold listing");
        }

        listingRepository.delete(listing);
    }

    // ============================
    // MY LISTINGS
    // ============================

    @Override
    public Page<ListingResponse> getMyListings(UUID userId, Pageable pageable) {

        User domainUser = findUserOrThrow(userId);

        return listingRepository.findByOwner(domainUser, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Page<ListingResponse> getMyActiveListings(UUID userId, Pageable pageable) {

        User domainUser = findUserOrThrow(userId);

        return listingRepository.findByOwnerAndStatus(domainUser, ListingStatus.ACTIVE, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Page<ListingResponse> getMySoldListings(UUID userId, Pageable pageable) {

        User domainUser = findUserOrThrow(userId);

        return listingRepository.findByOwnerAndStatus(domainUser, ListingStatus.SOLD, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Long countMyListings(UUID userId) {

        User domainUser = findUserOrThrow(userId);

        return listingRepository.countByOwner(domainUser);
    }

    // ============================
    // STATUS TRANSITIONS
    // ============================

    @Override
    public ListingResponse markAsSold(UUID id, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Only active listings can be marked as sold");
        }

        listing.setStatus(ListingStatus.SOLD);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse activateListing(UUID id, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.PAUSED) {
            throw new BusinessException("Only paused listings can be activated");
        }

        listing.setStatus(ListingStatus.ACTIVE);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse pauseListing(UUID id, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Only active listings can be paused");
        }

        listing.setStatus(ListingStatus.PAUSED);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse cancelListing(UUID id, UUID userId) {

        User domainUser = findUserOrThrow(userId);

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new BusinessException("Cannot cancel a sold listing");
        }

        listing.setStatus(ListingStatus.CANCELLED);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public Page<ListingResponse> getAllListings(Pageable pageable) {
        return listingRepository.findAll(pageable).map(listingMapper::toDto);
    }

    @Transactional
    @Override
    public void deleteListingByAdmin(UUID listingId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(LISTING_NOT_FOUND.formatted(listingId)));

        if (listing.getStatus() == ListingStatus.DELETED) {
            throw new BusinessException("Listing already deleted");
        }

        listing.setStatus(ListingStatus.DELETED);

        log.info("Listing {} soft deleted by admin", listingId);
    }

    @Transactional
    @Override
    public void forceCloseListing(UUID listingId) {

        Listing listing = findListingOrThrow(listingId);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Only active listings can be force closed");
        }

        listing.setStatus(ListingStatus.CLOSED);

        log.info("Listing {} force closed by admin", listingId);
    }


    // ============================
    // PRIVATE HELPER
    // ============================

    private Listing findListingOrThrow(UUID id) {
        return listingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(LISTING_NOT_FOUND.formatted(id)));
    }
    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Listing findOwnedListing(UUID id, User user) {

        Listing listing = findListingOrThrow(id);

        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You are not allowed to perform this operation");
        }

        return listing;
    }
}