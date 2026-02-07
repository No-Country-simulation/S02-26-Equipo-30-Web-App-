package com.nc.horseretail.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "validator_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidatorProfile {

    @Id
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validator_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidatorType validatorType; // VET, TRAINER

    private String licenseNumber;
    private String issuingAuthority;

    @Column(nullable = false)
    private boolean active;
}