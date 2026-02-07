package com.nc.horseretail.model.listing;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListingVerificationStatus {

    @Id
    private UUID listingId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OverallVerificationStatus overallStatus;

    @Column(nullable = false)
    private Instant lastUpdated;
}