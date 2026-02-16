package com.nc.horseretail.service;

import com.nc.horseretail.dto.ChatRequest;
import com.nc.horseretail.dto.ChatResponse;
import com.nc.horseretail.exception.ForbiddenOperationException;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.model.communication.Conversation;
import com.nc.horseretail.model.communication.ConversationStatus;
import com.nc.horseretail.model.communication.Message;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.ConversationRepository;
import com.nc.horseretail.repository.ListingRepository;
import com.nc.horseretail.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;

    @Transactional
    public ChatResponse sendMessage(ChatRequest request, User sender) {

        // Get Listing
        Listing listing = listingRepository.findById(request.getListingId()).orElseThrow(
                () -> new ResourceNotFoundException("Listing not found"));

        // Get or create Conversation
        Conversation conversation = conversationRepository.findByListingIdAndStartedById(
                listing.getId(), sender.getId()).orElseGet(
                        () -> createNewConversation(listing, sender));

        validateConversationAccess(conversation, sender);

        // Create message
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .messageText(request.getText())
                .sentAt(Instant.now())
                .build();

        Message saved = messageRepository.save(message);

        //TODO create Mapper
        return ChatResponse.builder()
                .messageId(saved.getId())
                .senderName(sender.getUsername())
                .text(saved.getMessageText())
                .sentAt(saved.getSentAt())
                .build();
    }

    private Conversation createNewConversation(Listing listing, User sender) {

        return conversationRepository.save(Conversation.builder()
                .listing(listing)
                .startedBy(sender)
                .status(ConversationStatus.OPEN)
                .createdAt(Instant.now())
                .build());
    }

    public List<ChatResponse> getHistory(UUID conversationId, User currentUser) {
        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(
                () -> new ResourceNotFoundException("Conversation not found"));

        validateConversationAccess(conversation, currentUser);

        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(m -> ChatResponse.builder()
                        .messageId(m.getId())
                        .senderName(m.getSender().getFullName())
                        .text(m.getMessageText())
                        .sentAt(m.getSentAt())
                        .build()).toList();
    }

    private void validateConversationAccess(Conversation conversation, User user) {

        UUID userId = user.getId();
        UUID buyerId = conversation.getStartedBy().getId();
        UUID sellerId = conversation.getListing().getOwner().getId();

        if (!userId.equals(buyerId) && !userId.equals(sellerId)) {
            throw new ForbiddenOperationException("You are not part of this conversation");
        }
    }
}