package com.nc.horseretail.model.horse;

import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_horses")
public class Horse {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private LocalDate birthDate;
    private String sex;
    private String breed;

    private Double heightM;
    private Double weightKg;
    private Double lengthM;
    private Double maxSpeedKmh;

    @Enumerated(EnumType.STRING)
    private Temperament temperament;
    @Enumerated(EnumType.STRING)
    private MainUse mainUse;
    private String lineage;
    private Integer careerRaces;
    private Integer daysSinceLastRace;

    private String birthCountry;

    @Embedded
    private Location location;

    private Boolean sellerVerified;
    private Integer sellerDisputes;
    private Boolean sellerFlaggedFraud;

    private Integer vetTotalExams;
    private Integer vetMajorIssues;

    private Double trustScore;

    @Enumerated(EnumType.STRING)
    private TrustScoreStatus trustScoreStatus;

    private Instant trustScoreUpdatedAt;

    private String trustModelVersion;
}
