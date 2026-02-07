package com.nc.horseretail.model.horse;

import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "horses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horse {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private LocalDate birthDate;
    private String sex;
    private String breed;

    private Double heightM;
    private Double weightKg;
    private Double lengthM;

    private Temperament temperament;
    private MainUse mainUse;
    private String lineage;

    private String birthCountry;
    private String currentCountry;

    @Embedded
    private Location location;
}