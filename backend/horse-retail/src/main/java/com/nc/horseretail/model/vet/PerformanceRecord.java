package com.nc.horseretail.model.vet;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "performance_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    private User validator;

    private String discipline;
    private String level;
    private LocalDate eventDate;

    private boolean verified;
}