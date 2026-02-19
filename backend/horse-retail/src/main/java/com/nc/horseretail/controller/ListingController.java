package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.ListingRequest;
import com.nc.horseretail.dto.ListingResponse;
import com.nc.horseretail.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Listing", description = "API for managing horse listings")
@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@Slf4j
public class ListingController {

    private final ListingService listingService;

    // ============================
    // CREATE LISTING
    // ============================
    @Operation(
            summary = "Create listing",
            description = "Creates a new listing for a horse owned by the authenticated user"
    )
    @ApiResponse(responseCode = "201", description = "Listing created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(
            @Valid @RequestBody ListingRequest dto,
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Creating listing for user {}", securityUser.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listingService.createListing(dto, securityUser.getDomainUser()));
    }

    // ============================
    // GET / SEARCH LISTINGS
    // ============================
    @Operation(
            summary = "Get or search listings",
            description = "Returns paginated listings. Supports optional keyword filtering and sorting."
    )
    @ApiResponse(responseCode = "200", description = "Listings retrieved successfully")
    @GetMapping
    public ResponseEntity<Page<ListingResponse>> getListings(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        log.info("Fetching listings. keyword={}", keyword);

        return ResponseEntity.ok(
                listingService.getListings(keyword, pageable)
        );
    }

    // ============================
    // GET LISTING BY ID
    // ============================
    @Operation(
            summary = "Get listing by ID",
            description = "Returns detailed information about a specific listing"
    )
    @ApiResponse(responseCode = "200", description = "Listing retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(@PathVariable UUID id) {

        log.info("Fetching listing {}", id);

        return ResponseEntity.ok(
                listingService.getListingById(id)
        );
    }

    // ============================
    // UPDATE LISTING
    // ============================
    @Operation(
            summary = "Update listing",
            description = "Updates a listing owned by the authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Listing updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - not owner")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListing(
            @PathVariable UUID id,
            @Valid @RequestBody ListingRequest dto,
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Updating listing {}", id);

        return ResponseEntity.ok(
                listingService.updateListing(id, dto, securityUser.getDomainUser())
        );
    }

    // ============================
    // DELETE LISTING
    // ============================
    @Operation(
            summary = "Delete listing",
            description = "Deletes a listing owned by the authenticated user"
    )
    @ApiResponse(responseCode = "204", description = "Listing deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - not owner")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {

        log.info("Deleting listing {}", id);

        listingService.deleteListing(id, securityUser.getDomainUser());

        return ResponseEntity.noContent().build();
    }

    // ============================
    // ACTIVATE LISTING
    // ============================
    @Operation(
            summary = "Activate listing",
            description = "Activates a paused or inactive listing"
    )
    @ApiResponse(responseCode = "200", description = "Listing activated")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ListingResponse> activateListing(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                listingService.activateListing(id, securityUser.getDomainUser())
        );
    }

    // ============================
    // PAUSE LISTING
    // ============================
    @Operation(
            summary = "Pause listing",
            description = "Pauses an active listing"
    )
    @ApiResponse(responseCode = "200", description = "Listing paused")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @PatchMapping("/{id}/pause")
    public ResponseEntity<ListingResponse> pauseListing(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                listingService.pauseListing(id, securityUser.getDomainUser())
        );
    }

    // ============================
    // MARK LISTING AS SOLD
    // ============================
    @Operation(
            summary = "Mark listing as sold",
            description = "Marks a listing as sold"
    )
    @ApiResponse(responseCode = "200", description = "Listing marked as sold")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @PatchMapping("/{id}/sold")
    public ResponseEntity<ListingResponse> markAsSold(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                listingService.markAsSold(id, securityUser.getDomainUser())
        );
    }

    // ============================
    // CANCEL LISTING
    // ============================
    @Operation(
            summary = "Cancel listing",
            description = "Cancels a listing"
    )
    @ApiResponse(responseCode = "200", description = "Listing cancelled")
    @ApiResponse(responseCode = "404", description = "Listing not found")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ListingResponse> cancelListing(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                listingService.cancelListing(id, securityUser.getDomainUser())
        );
    }

    // ============================
    // GET MY LISTINGS
    // ============================
    @Operation(
            summary = "Get my listings",
            description = "Returns paginated listings owned by authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Listings retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<Page<ListingResponse>> getMyListings(
            @AuthenticationPrincipal SecurityUser securityUser,
            Pageable pageable) {

        return ResponseEntity.ok(
                listingService.getMyListings(securityUser.getDomainUser(), pageable)
        );
    }

    // ============================
    // GET MY ACTIVE LISTINGS
    // ============================
    @Operation(
            summary = "Get my active listings",
            description = "Returns paginated active listings owned by authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Listings retrieved successfully")
    @GetMapping("/me/active")
    public ResponseEntity<Page<ListingResponse>> getMyActiveListings(
            @AuthenticationPrincipal SecurityUser securityUser,
            Pageable pageable) {

        return ResponseEntity.ok(
                listingService.getMyActiveListings(securityUser.getDomainUser(), pageable)
        );
    }

    // ============================
    // GET MY SOLD LISTINGS
    // ============================
    @Operation(
            summary = "Get my sold listings",
            description = "Returns paginated sold listings owned by authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Listings retrieved successfully")
    @GetMapping("/me/sold")
    public ResponseEntity<Page<ListingResponse>> getMySoldListings(
            @AuthenticationPrincipal SecurityUser securityUser,
            Pageable pageable) {

        return ResponseEntity.ok(
                listingService.getMySoldListings(securityUser.getDomainUser(), pageable)
        );
    }

    // ============================
    // COUNT MY LISTINGS
    // ============================
    @Operation(
            summary = "Count my listings",
            description = "Returns total number of listings owned by authenticated user"
    )
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    @GetMapping("/me/count")
    public ResponseEntity<Long> countMyListings(
            @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(
                listingService.countMyListings(securityUser.getDomainUser())
        );
    }
}