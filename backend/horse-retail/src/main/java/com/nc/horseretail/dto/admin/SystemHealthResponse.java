package com.nc.horseretail.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class SystemHealthResponse {

    private String status;
    private long uptimeSeconds;
    private long totalUsers;
    private long totalListings;
    private long totalHorses;
    private long totalConversations;
    private long totalMessages;
    private Instant serverTime;
}