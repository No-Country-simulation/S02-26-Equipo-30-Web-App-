package com.nc.horseretail.service;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.exception.BusinessException;
import com.nc.horseretail.exception.ForbiddenOperationException;
import com.nc.horseretail.mapper.ListingMapper;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.listing.ListingStatus;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.HorseRepository;
import com.nc.horseretail.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {
    private final ListingMapper listingMapper;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;

    @Override
    public ListingResponse createListing(ListingRequest dto, User currentUser) {

        Horse horse = horseRepository.findById(dto.getHorseId()).orElseThrow(
                () -> new IllegalArgumentException("Horse not found with id: " + dto.getHorseId())
        );

        if(!horse.getOwner().getId().equals(currentUser.getId())){
            throw new ForbiddenOperationException("You are not allowed to perform this operation");
        }

        if (listingRepository.existsByHorseIdAndStatus(horse.getId(), ListingStatus.ACTIVE)) {
            throw new BusinessException("Horse already has an active listing");
        }

        Listing listing = listingMapper.toEntity(dto);

        listing.setOwner(currentUser);
        listing.setHorse(horse);

        //TODO Eliminar si quitamos externalId
        listing.setExternalId(UUID.randomUUID().toString());

        Listing savedListing = listingRepository.save(listing);
        return listingMapper.toDto(savedListing);
    }
}
