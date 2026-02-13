package com.nc.horseretail.controller;

import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.UserRepository;
import com.nc.horseretail.repository.HorseRepository; // AGREGAR
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final HorseRepository horseRepository; // AGREGAR

    @PostMapping
    public ListingResponse createListing(@RequestBody ListingRequest dto) {
        User owner = userRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Buscar el caballo usando el ID del DTO
        Horse horse = horseRepository.findById(dto.getHorseId())
                .orElseThrow(() -> new RuntimeException("Horse not found"));

        Listing listing = Listing.builder()
                .owner(owner)
                .horse(horse)
                .askingPriceUsd(dto.getPrice())
                .build();
                
        Listing savedListing = listingRepository.save(listing);

        return ListingResponse.builder()
                .id(savedListing.getId())
                .ownerId(owner.getId())
                .ownerName(owner.getFullName())
                .price(savedListing.getAskingPriceUsd())
                .status(savedListing.getStatus().toString())
                .build();
    }
}