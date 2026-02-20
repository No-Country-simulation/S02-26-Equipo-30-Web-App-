package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.messaging.ConversationDetailResponse;
import com.nc.horseretail.dto.messaging.ConversationSummaryResponse;
import com.nc.horseretail.dto.messaging.MessageResponse;
import com.nc.horseretail.dto.messaging.SendMessageRequest;
import com.nc.horseretail.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "User messaging system")
public class ChatController {

    private final ChatService chatService;

    // ============================
    // CREATE CONVERSATION
    // ============================
    @Operation(summary = "Start a conversation for a listing")
    @ApiResponse(responseCode = "201", description = "Conversation created successfully")
    @PostMapping("/conversations")
    public ResponseEntity<ConversationDetailResponse> createConversation(
            @RequestParam UUID listingId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.createConversation(listingId, user.getDomainUser()));
    }

    // ============================
    // SEND MESSAGE
    // ============================
    @Operation(summary = "Send a message in a conversation")
    @ApiResponse(responseCode = "201", description = "Message sent successfully")
    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatService.sendMessage(conversationId, request, user.getDomainUser()));
    }

    // ============================
    // GET USER CONVERSATIONS
    // ============================
    @Operation(summary = "Get all conversations for the authenticated user")
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryResponse>> getUserConversations(
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.getUserConversations(user.getDomainUser())
        );
    }

    // ============================
    // GET CONVERSATION DETAILS
    // ============================
    @Operation(summary = "Get conversation details")
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.getConversation(conversationId, user.getDomainUser())
        );
    }

    // ============================
    // GET CONVERSATION MESSAGES
    // ============================
    @Operation(summary = "Get messages of a conversation")
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.getMessages(conversationId, user.getDomainUser())
        );
    }

    // ============================
    // MARK MESSAGE AS READ
    // ============================
    @PatchMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID messageId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        chatService.markMessageAsRead(messageId, user.getDomainUser());
        return ResponseEntity.noContent().build();
    }

    // ============================
    // CLOSE CONVERSATION
    // ============================
    @PatchMapping("/conversations/{conversationId}/close")
    public ResponseEntity<Void> closeConversation(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        chatService.closeConversation(conversationId, user.getDomainUser());
        return ResponseEntity.noContent().build();
    }

    // ============================
    // UNREAD COUNT
    // ============================
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.getUnreadCount(user.getDomainUser())
        );
    }
}