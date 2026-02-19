package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.model.user.User;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListingService {
    ListingResponse createListing(ListingRequest dto, User domainUser);

    Page<ListingResponse> getListings(String keyword, Pageable pageable);

    ListingResponse getListingById(UUID id);

    ListingResponse updateListing(UUID id, @Valid ListingRequest dto, User domainUser);

    void deleteListing(UUID id, User domainUser);

    Page<ListingResponse> getMyListings(User domainUser, Pageable pageable);

    Long countMyListings(User domainUser);

    Page<ListingResponse> getMyActiveListings(User domainUser, Pageable pageable);

    Page<ListingResponse> getMySoldListings(User domainUser, Pageable pageable);

    ListingResponse markAsSold(UUID id, User domainUser);

    ListingResponse activateListing(UUID id, User domainUser);

    ListingResponse pauseListing(UUID id, User domainUser);

    ListingResponse cancelListing(UUID id, User domainUser);
}
