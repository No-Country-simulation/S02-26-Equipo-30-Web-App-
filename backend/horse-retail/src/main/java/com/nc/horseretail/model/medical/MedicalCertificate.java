package com.nc.horseretail.model.medical;

import com.nc.horseretail.model.horse.Horse;
import com.nc.horseretail.model.user.Veterinarian;
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
public class MedicalCertificate {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", nullable = false)
    private Veterinarian issuedBy;

    @Column(nullable = false)
    private String certificateType;

    private LocalDate issueDate;
    private LocalDate validUntil;
}