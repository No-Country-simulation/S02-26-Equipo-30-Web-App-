package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ChatResponse {
    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String text;
    private Instant sentAt;
}