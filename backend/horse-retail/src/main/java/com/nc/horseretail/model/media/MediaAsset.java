package com.nc.horseretail.model.media;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tbl_media_assets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAsset {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaVisibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id")
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_id", nullable = false)
    private User uploadedBy;

    private LocalDate captureDate;

    private String context;

    private boolean unedited;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    public void prePersist() {
        this.uploadedAt = Instant.now();
    }
}