package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class MessageResponse {

    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String text;
    private Instant sentAt;
}
