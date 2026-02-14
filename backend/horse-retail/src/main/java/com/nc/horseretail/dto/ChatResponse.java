package com.nc.horseretail.dto.communication;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter 
@Builder
public class ChatResponse {
    private UUID messageId;
    private String senderName;
    private String text;
    private Instant sentAt;
}