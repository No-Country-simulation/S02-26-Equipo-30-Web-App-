package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ForbiddenOperationException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.mapper.ListingMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingMapper listingMapper;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;

    // ============================
    // CREATE
    // ============================

    @Override
    public ListingResponse createListing(ListingRequest dto, User currentUser) {

        Horse horse = horseRepository.findById(dto.getHorseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Horse not found with id: " + dto.getHorseId()));

        if (!horse.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenOperationException("You are not allowed to perform this operation");
        }

        if (listingRepository.existsByHorseIdAndStatus(horse.getId(), ListingStatus.ACTIVE)) {
            throw new BusinessException("Horse already has an active listing");
        }

        Listing listing = listingMapper.toEntity(dto);

        listing.setOwner(currentUser);
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

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Listing not found with id: " + id));

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new ResourceNotFoundException("Listing not found with id: " + id);
        }

        return listingMapper.toDto(listing);
    }

    // ============================
    // UPDATE
    // ============================

    @Override
    public ListingResponse updateListing(UUID id, ListingRequest dto, User domainUser) {

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
    public void deleteListing(UUID id, User domainUser) {

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
    public Page<ListingResponse> getMyListings(User domainUser, Pageable pageable) {
        return listingRepository.findByOwner(domainUser, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Page<ListingResponse> getMyActiveListings(User domainUser, Pageable pageable) {
        return listingRepository.findByOwnerAndStatus(domainUser, ListingStatus.ACTIVE, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Page<ListingResponse> getMySoldListings(User domainUser, Pageable pageable) {
        return listingRepository.findByOwnerAndStatus(domainUser, ListingStatus.SOLD, pageable)
                .map(listingMapper::toDto);
    }

    @Override
    public Long countMyListings(User domainUser) {
        return listingRepository.countByOwner(domainUser);
    }

    // ============================
    // STATUS TRANSITIONS
    // ============================

    @Override
    public ListingResponse markAsSold(UUID id, User domainUser) {

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Only active listings can be marked as sold");
        }

        listing.setStatus(ListingStatus.SOLD);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse activateListing(UUID id, User domainUser) {

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.PAUSED) {
            throw new BusinessException("Only paused listings can be activated");
        }

        listing.setStatus(ListingStatus.ACTIVE);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse pauseListing(UUID id, User domainUser) {

        Listing listing = findOwnedListing(id, domainUser);

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new BusinessException("Only active listings can be paused");
        }

        listing.setStatus(ListingStatus.PAUSED);

        return listingMapper.toDto(listingRepository.save(listing));
    }

    @Override
    public ListingResponse cancelListing(UUID id, User domainUser) {

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

    @Override
    public void deleteListingByAdmin(UUID listingId) {
        //TODO implement method
        throw new BusinessException("Method not implemented yet");
    }

    @Override
    public void forceCloseListing(UUID listingId) {
        //TODO implement method
        throw new BusinessException("Method not implemented yet");
    }

    // ============================
    // PRIVATE HELPER
    // ============================

    private Listing findOwnedListing(UUID id, User user) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Listing not found with id: " + id));

        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You are not allowed to perform this operation");
        }

        return listing;
    }
}