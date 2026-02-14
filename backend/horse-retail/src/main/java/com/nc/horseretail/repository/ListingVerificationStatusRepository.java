package com.nc.horseretail.repository;

import com.nc.horseretail.model.listing.ListingVerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingVerificationStatusRepository extends JpaRepository<ListingVerificationStatus, UUID> {
}
