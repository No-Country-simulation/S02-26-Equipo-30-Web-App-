package com.nc.horseretail.service;

import com.nc.horseretail.dto.admin.AuditLogResponse;
import com.nc.horseretail.dto.admin.SystemHealthResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminSystemService {

    SystemHealthResponse getSystemHealth();

    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
}
