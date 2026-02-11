package com.nc.horseretail.model.listing;

import com.nc.horseretail.model.horse.Horse;
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
@Table(name = "tbl_listings")
public class Listing {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    private Double askingPriceUsd;

    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    private Instant createdAt;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}