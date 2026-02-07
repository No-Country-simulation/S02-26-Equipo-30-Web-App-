package com.nc.horseretail.model.communication;

import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
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
public class Conversation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "started_by", nullable = false)
    private User startedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}