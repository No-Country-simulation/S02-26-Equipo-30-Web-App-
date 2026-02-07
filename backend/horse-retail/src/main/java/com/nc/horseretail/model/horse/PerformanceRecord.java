package com.nc.horseretail.model.horse;

import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_performance_records")
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