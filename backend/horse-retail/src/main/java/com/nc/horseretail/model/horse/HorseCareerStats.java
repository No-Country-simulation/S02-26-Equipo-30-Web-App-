package com.nc.horseretail.model.horse;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "horse_career_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseCareerStats {

    @Id
    private UUID horseId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id")
    private Horse horse;

    private Integer careerRaces;
    private Double careerTop3Rate;
    private Integer daysSinceLastRace;
}
