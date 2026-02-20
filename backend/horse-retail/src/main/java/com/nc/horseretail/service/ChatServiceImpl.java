package com.nc.horseretail.service;

import com.nc.horseretail.dto.messaging.ConversationDetailResponse;
import com.nc.horseretail.dto.messaging.ConversationSummaryResponse;
import com.nc.horseretail.dto.messaging.MessageResponse;
import com.nc.horseretail.dto.messaging.SendMessageRequest;
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
    public MessageResponse sendMessage(UUID conversationId,
                                       SendMessageRequest request,
                                       User sender) {

        Conversation conversation = getConversationOrThrow(conversationId);

        validateAccess(conversation, sender);

        if (conversation.getStatus() == ConversationStatus.CLOSED) {
            throw new IllegalStateException("Conversation is closed");
        }

        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .messageText(request.getText())
                .sentAt(Instant.now())
                .read(false)
                .build();

        Message saved = messageRepository.save(message);

        conversation.setUpdatedAt(Instant.now());

        return mapMessage(saved);
    }

    // =============================
    // CREATE CONVERSATION
    // =============================
    @Override
    public ConversationDetailResponse createConversation(UUID listingId, User user) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        if (listing.getOwner().getId().equals(user.getId())) {
            throw new IllegalStateException("You cannot start a conversation on your own listing");
        }

        Conversation conversation = conversationRepository
                .findByListingIdAndStartedById(listingId, user.getId())
                .orElseGet(() -> conversationRepository.save(
                        Conversation.builder()
                                .listing(listing)
                                .startedBy(user)
                                .status(ConversationStatus.OPEN)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .build()
                ));

        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .listingId(listing.getId())
                .listingTitle(listing.getHorse().getName())
                .messages(List.of())
                .build();
    }

    // =============================
    // LIST USER CONVERSATIONS
    // =============================
    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> getUserConversations(User user) {

        List<Conversation> conversations =
                conversationRepository.findUserConversations(user.getId());

        return conversations.stream().map(c -> {

            User otherUser = resolveOtherUser(c, user);

            String lastMessage = messageRepository
                    .findTopByConversationIdOrderBySentAtDesc(c.getId())
                    .map(Message::getMessageText)
                    .orElse("");

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
    // GET CONVERSATION
    // =============================
    @Override
    @Transactional(readOnly = true)
    public ConversationDetailResponse getConversation(UUID conversationId, User user) {

        Conversation conversation = getConversationOrThrow(conversationId);

        validateAccess(conversation, user);

        List<MessageResponse> messages =
                messageRepository.findConversationMessages(conversationId)
                        .stream()
                        .map(this::mapMessage)
                        .toList();

        return ConversationDetailResponse.builder()
                .conversationId(conversation.getId())
                .listingId(conversation.getListing().getId())
                .listingTitle(conversation.getListing().getHorse().getName())
                .messages(messages)
                .build();
    }

    // =============================
    // GET MESSAGES ONLY
    // =============================
    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(UUID conversationId, User user) {

        Conversation conversation = getConversationOrThrow(conversationId);

        validateAccess(conversation, user);

        return messageRepository.findConversationMessages(conversationId)
                .stream()
                .map(this::mapMessage)
                .toList();
    }

    // =============================
    // MARK AS READ
    // =============================
    @Override
    public void markMessageAsRead(UUID messageId, User user) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        validateAccess(message.getConversation(), user);

        if (!message.getSender().getId().equals(user.getId())) {
            message.setRead(true);
        }
    }

    // =============================
    // CLOSE CONVERSATION
    // =============================
    @Override
    public void closeConversation(UUID conversationId, User user) {

        Conversation conversation = getConversationOrThrow(conversationId);

        validateAccess(conversation, user);

        conversation.setStatus(ConversationStatus.CLOSED);
        conversation.setUpdatedAt(Instant.now());
    }

    // =============================
    // UNREAD COUNT
    // =============================
    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(User user) {

        return messageRepository.countUnreadMessages(user.getId());
    }

    // =============================
    // PRIVATE HELPERS
    // =============================

    private Conversation getConversationOrThrow(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
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