package com.nc.horseretail.dto.messaging;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ConversationSummaryResponse {

    private UUID conversationId;
    private UUID listingId;
    private String listingTitle;
    private String otherUserName;
    private String lastMessage;
    private Instant updatedAt;
}