package com.nc.horseretail.repository;

import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {

    boolean existsByHorseIdAndStatus(UUID horseId, ListingStatus status);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    @Query("""
    SELECT l FROM Listing l
    WHERE l.status = 'ACTIVE'
    AND (
        :keyword IS NULL OR
        LOWER(l.horse.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
""")
    Page<Listing> searchActive(String keyword, Pageable pageable);

    Page<Listing> findByOwner(User owner, Pageable pageable);

    Page<Listing> findByOwnerAndStatus(User owner, ListingStatus status, Pageable pageable);

    long countByOwner(User owner);
}
