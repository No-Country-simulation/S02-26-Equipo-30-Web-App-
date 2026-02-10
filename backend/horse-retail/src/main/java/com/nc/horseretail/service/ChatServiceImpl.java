package com.nc.horseretail.service;

import com.nc.horseretail.dto.communication.ChatRequest;
import com.nc.horseretail.dto.communication.ChatResponse;
import com.nc.horseretail.exception.ResourceNotFoundException;
import com.nc.horseretail.model.communication.Conversation;
import com.nc.horseretail.model.communication.ConversationStatus;
import com.nc.horseretail.model.communication.Message;
import com.nc.horseretail.model.listing.Listing;
import com.nc.horseretail.model.user.User;
import com.nc.horseretail.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    @Transactional
    public ChatResponse sendMessage(ChatRequest request) {
        // Buscar o Crear la conversación
        Conversation conversation = conversationRepository
            .findByListingIdAndStartedById(request.getListingId(), request.getSenderId())
            .orElseGet(() -> createNewConversation(request.getListingId(), request.getSenderId()));

        // Buscar al usuario que envía
        User sender = userRepository.findById(request.getSenderId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Crear el mensaje
        Message message = Message.builder()
            .conversation(conversation)
            .sender(sender)
            .messageText(request.getText())
            .sentAt(Instant.now())
            .build();

        Message saved = messageRepository.save(message);

        return ChatResponse.builder()
            .messageId(saved.getId())
            .senderName(sender.getFullName())
            .text(saved.getMessageText())
            .sentAt(saved.getSentAt())
            .build();
    }

    private Conversation createNewConversation(UUID listingId, UUID userId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return conversationRepository.save(Conversation.builder()
            .listing(listing)
            .startedBy(user)
            .status(ConversationStatus.OPEN)
            .createdAt(Instant.now())
            .build());
    }

    public List<ChatResponse> getHistory(UUID conversationId) {
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
            .stream()
            .map(m -> ChatResponse.builder()
                .messageId(m.getId())
                .senderName(m.getSender().getFullName())
                .text(m.getMessageText())
                .sentAt(m.getSentAt())
                .build())
            .collect(Collectors.toList());
    }
}
