package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class ConversationSummaryResponse {

    private UUID conversationId;
    private UUID listingId;
    private String listingTitle;
    private String otherUserName;
    private String lastMessage;
    private Instant updatedAt;
}