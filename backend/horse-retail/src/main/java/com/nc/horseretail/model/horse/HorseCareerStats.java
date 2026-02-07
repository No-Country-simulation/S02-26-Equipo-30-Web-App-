package com.nc.horseretail.model.horse;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_horse_career_stats")
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
