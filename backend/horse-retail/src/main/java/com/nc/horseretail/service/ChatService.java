package com.nc.horseretail.service;

import com.nc.horseretail.dto.messaging.ConversationDetailResponse;
import com.nc.horseretail.dto.messaging.ConversationSummaryResponse;
import com.nc.horseretail.dto.messaging.MessageResponse;
import com.nc.horseretail.dto.messaging.SendMessageRequest;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    MessageResponse sendMessage(UUID conversationId, SendMessageRequest request, UUID userId);

    List<ConversationSummaryResponse> getUserConversations(UUID userId);

    ConversationDetailResponse getConversation(UUID conversationId, UUID userId);

    ConversationDetailResponse createConversation(UUID listingId, UUID userId);

    List<MessageResponse> getMessages(UUID conversationId, UUID userId);

    void markMessageAsRead(UUID messageId, UUID userId);

    void closeConversation(UUID conversationId, UUID userId);

    Long getUnreadCount(UUID userId);
}