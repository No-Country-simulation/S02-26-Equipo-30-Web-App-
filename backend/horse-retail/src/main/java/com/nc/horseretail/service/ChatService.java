package com.nc.horseretail.service;

import com.nc.horseretail.dto.ConversationDetailResponse;
import com.nc.horseretail.dto.ConversationSummaryResponse;
import com.nc.horseretail.dto.MessageResponse;
import com.nc.horseretail.dto.SendMessageRequest;
import com.nc.horseretail.model.user.User;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    MessageResponse sendMessage(UUID conversationId, SendMessageRequest request, User sender);

    List<ConversationSummaryResponse> getUserConversations(User user);

    ConversationDetailResponse getConversation(UUID conversationId, User user);

    ConversationDetailResponse createConversation(UUID listingId, User domainUser);

    List<MessageResponse> getMessages(UUID conversationId, User domainUser);

    void markMessageAsRead(UUID messageId, User domainUser);

    void closeConversation(UUID conversationId, User domainUser);

    Long getUnreadCount(User domainUser);
}