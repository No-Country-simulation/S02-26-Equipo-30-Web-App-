package com.nc.horseretail.repository;

import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {

    boolean existsByHorseIdAndStatus(UUID horseId, ListingStatus status);
}
