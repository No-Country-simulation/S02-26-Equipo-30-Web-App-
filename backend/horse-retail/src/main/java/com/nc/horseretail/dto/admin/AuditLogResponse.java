package com.nc.horseretail.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuditLogResponse {

    private UUID id;
    private String action;
    private String entityType;
    private UUID entityId;
    private String performedBy;
    private Instant timestamp;
}