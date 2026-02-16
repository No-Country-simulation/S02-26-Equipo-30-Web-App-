package com.nc.horseretail.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ConversationDetailResponse {

    private UUID conversationId;
    private UUID listingId;
    private String listingTitle;
    private List<MessageResponse> messages;
}
