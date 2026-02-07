package com.nc.horseretail.model.market;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPriceSnapshot {

    @Id
    @GeneratedValue
    private UUID id;

    private String breed;
    private String discipline;
    private String ageRange;
    private String region;

    @Column(precision = 15, scale = 2)
    private BigDecimal medianPriceUsd;

    private LocalDate snapshotDate;
}