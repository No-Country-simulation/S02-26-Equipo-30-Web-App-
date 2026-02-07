package com.nc.horseretail.model.transaction;

import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tbl_purchase_intents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseIntent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    @Enumerated(EnumType.STRING)
    private PurchaseIntentStatus status;

    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
