package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    //TODO POST /  → AUTH
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@RequestBody ListingRequest dto,
                                                         @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(dto, securityUser.getDomainUser()));
    }

    //TODO GET /  → PUBLIC (paginado + filtros)
    //TODO GET /{listingId}  → PUBLIC
    //TODO PUT /{listingId}  → AUTH (owner only)
    //TODO DELETE /{listingId}  → AUTH (owner only)
    //TODO PATCH /{listingId}/activate  → AUTH (owner)
    //TODO PATCH /{listingId}/pause  → AUTH (owner)
    //TODO PATCH /{listingId}/sold  → AUTH (owner)
    //TODO PATCH /{listingId}/cancel  → AUTH (owner)
    //TODO GET /me  → AUTH
    //TODO GET /me/active  → AUTH
    //TODO GET /me/sold  → AUTH
    //TODO GET /me/count  → AUTH
    //TODO GET /search  → PUBLIC
}