package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.model.user.User;

public interface ListingService {
    ListingResponse createListing(ListingRequest dto, User domainUser);
}
