package com.nc.horseretail.service;

import com.nc.horseretail.dto.*;
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
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ListingRepository listingRepository;

    // =============================
    // SEND MESSAGE
    // =============================

    @Override
    public MessageResponse sendMessage(SendMessageRequest request, User sender) {

        Listing listing = listingRepository.findById(request.getListingId()).orElseThrow(
                () -> new ResourceNotFoundException("Listing not found"));

        Conversation conversation = conversationRepository.findByListingIdAndStartedById(
                listing.getId(), sender.getId()).orElseGet(
                        () -> createConversation(listing, sender));

        validateAccess(conversation, sender);

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .messageText(request.getText())
                .sentAt(Instant.now()).build();

        Message saved = messageRepository.save(message);

        conversation.setUpdatedAt(Instant.now());

        return mapMessage(saved);
    }

    // =============================
    // LIST USER CONVERSATIONS
    // =============================

    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getUserConversations(User user) {

        List<Conversation> conversations = conversationRepository.findUserConversations(user.getId());

        return conversations.stream().map(c -> {

            User otherUser = resolveOtherUser(c, user);

            String lastMessage = messageRepository.findTopByConversationIdOrderBySentAtDesc(c.getId())
                    .map(Message::getMessageText).orElse("");

            return ConversationSummaryResponse.builder()
                    .conversationId(c.getId())
                    .listingId(c.getListing().getId())
                    .listingTitle(c.getListing().getHorse().getName())
                    .otherUserName(otherUser.getFullName())
                    .lastMessage(lastMessage)
                    .updatedAt(c.getUpdatedAt())
                    .build();
        }).toList();
    }

    // =============================
    // GET FULL CONVERSATION
    // =============================

    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversation(UUID conversationId, User user) {

        Conversation conversation = conversationRepository.findById(conversationId).orElseThrow(
                () -> new ResourceNotFoundException("Conversation not found"));

        validateAccess(conversation, user);

        List<MessageResponse> messages = messageRepository.findConversationMessages(conversationId)
                .stream().map(this::mapMessage).toList();

        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .listingId(conversation.getListing().getId())
                .listingTitle(conversation.getListing().getHorse().getName())
                .messages(messages)
                .build();
    }

    // =============================
    // PRIVATE HELPERS
    // =============================

    private Conversation createConversation(Listing listing, User buyer) {

        return conversationRepository.save(Conversation.builder()
                .listing(listing)
                .startedBy(buyer)
                .status(ConversationStatus.OPEN)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());
    }

    private void validateAccess(Conversation conversation, User user) {

        UUID userId = user.getId();
        UUID buyerId = conversation.getStartedBy().getId();
        UUID sellerId = conversation.getListing().getOwner().getId();

        if (!userId.equals(buyerId) && !userId.equals(sellerId)) {
            throw new ForbiddenOperationException("You are not part of this conversation");
        }
    }

    private User resolveOtherUser(Conversation conversation, User currentUser) {

        if (conversation.getStartedBy().getId().equals(currentUser.getId())) {
            return conversation.getListing().getOwner();
        }

        return conversation.getStartedBy();
    }

    private MessageResponse mapMessage(Message message) {

        return MessageResponse.builder()
                .messageId(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFullName())
                .text(message.getMessageText())
                .sentAt(message.getSentAt())
                .build();
    }
}