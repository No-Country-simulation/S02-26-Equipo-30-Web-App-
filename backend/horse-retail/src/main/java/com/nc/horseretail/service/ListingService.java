package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingFilterRequest;
import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListingService {
    ListingResponse createListing(ListingRequest dto, UUID userId);

    Page<ListingResponse> getListings(String keyword, Pageable pageable);

    ListingResponse getListingById(UUID id);

    Page<ListingResponse> searchListings(
            ListingFilterRequest filter,
            Pageable pageable);

    ListingResponse updateListing(UUID id, @Valid ListingRequest dto, UUID userId);

    void deleteListing(UUID id, UUID userId);

    Page<ListingResponse> getMyListings(UUID userId, Pageable pageable);

    Long countMyListings(UUID userId);

    Page<ListingResponse> getMyActiveListings(UUID userId, Pageable pageable);

    Page<ListingResponse> getMySoldListings(UUID userId, Pageable pageable);

    ListingResponse markAsSold(UUID id, UUID userId);

    ListingResponse activateListing(UUID id, UUID userId);

    ListingResponse pauseListing(UUID id, UUID userId);

    ListingResponse cancelListing(UUID id, UUID userId);

    Page<ListingResponse> getAllListings(Pageable pageable);

    void deleteListingByAdmin(UUID listingId);

    void forceCloseListing(UUID listingId);
}
