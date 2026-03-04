package com.nc.horseretail.repository;

import com.nc.horseretail.model.media.MediaAsset;
import com.nc.horseretail.model.media.MediaVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {


    List<MediaAsset> findByHorseId(UUID horseId);

    List<MediaAsset> findByHorseIdInAndVisibility(Collection<UUID> horseIds, MediaVisibility visibility);

    List<MediaAsset> findByListingId(UUID listingId);
}
