package com.nc.horseretail.service;

import com.nc.horseretail.dto.admin.AuditLogResponse;
import com.nc.horseretail.dto.admin.SystemHealthResponse;
import com.nc.horseretail.model.audit.AuditLog;
import com.nc.horseretail.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminSystemServiceImpl implements AdminSystemService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final HorseRepository horseRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AuditLogRepository auditLogRepository;

    private final Instant applicationStartTime = Instant.now();

    // =============================
    // SYSTEM HEALTH
    // =============================
    @Override
    public SystemHealthResponse getSystemHealth() {

        long totalUsers = userRepository.count();
        long totalListings = listingRepository.count();
        long totalHorses = horseRepository.count();
        long totalConversations = conversationRepository.count();
        long totalMessages = messageRepository.count();

        Duration uptime = Duration.between(applicationStartTime, Instant.now());

        return SystemHealthResponse.builder()
                .status("UP")
                .uptimeSeconds(uptime.getSeconds())
                .totalUsers(totalUsers)
                .totalListings(totalListings)
                .totalHorses(totalHorses)
                .totalConversations(totalConversations)
                .totalMessages(totalMessages)
                .serverTime(Instant.now())
                .build();
    }

    // =============================
    // AUDIT LOGS
    // =============================
    @Override
    public Page<AuditLogResponse> getAuditLogs(Pageable pageable) {

        Page<AuditLog> logs = auditLogRepository.findAllByOrderByPerformedAtDesc(pageable);

        return logs.map(log ->
                AuditLogResponse.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .entityType(log.getEntityType())
                        .entityId(log.getEntityId())
                        .performedBy(
                                log.getPerformedBy() != null
                                        ? log.getPerformedBy().getFullName()
                                        : "SYSTEM"
                        )
                        .timestamp(log.getPerformedAt())
                        .build()
        );
    }
}