package com.nc.horseretail.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_veterinarians")
public class Veterinarian {

    @Id
    private UUID id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vet_id")
    private ValidatorProfile profile;

    private String specialization;
    private String country;
    private Integer yearsExperience;
}