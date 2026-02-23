package com.nc.horseretail.model.audit;

import com.nc.horseretail.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tbl_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    private String entityType;
    private UUID entityId;
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    private Instant performedAt;

    @PrePersist
    public void prePersist() {
        this.performedAt = Instant.now();
    }
}