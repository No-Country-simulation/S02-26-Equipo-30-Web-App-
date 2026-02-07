package com.nc.horseretail.model.medical;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "veterinary_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinaryRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    private User vet;

    private String examType;
    private LocalDate examDate;

    private boolean majorIssue;
    private boolean verified;
}