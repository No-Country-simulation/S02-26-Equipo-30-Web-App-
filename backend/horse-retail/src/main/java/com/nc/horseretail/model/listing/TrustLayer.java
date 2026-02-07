package com.nc.horseretail.model.listing;

import com.nc.horseretail.model.vet.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trust_layers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustLayer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Listing listing;

    @Enumerated(EnumType.STRING)
    private TrustLayerType layerType;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    private Instant verifiedAt;
}