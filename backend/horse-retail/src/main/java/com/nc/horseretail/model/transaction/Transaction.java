package com.nc.horseretail.model.transaction;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tbl_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    private User buyer;

    private Double finalPriceUsd;

    private Instant completedAt;
}