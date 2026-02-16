package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.*;
import com.nc.horseretail.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // ============================
    // SEND MESSAGE
    // ============================
    @Operation(summary = "Send a message in a conversation")
    @ApiResponse(responseCode = "200", description = "Message sent successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Unauthorized to send message in this conversation")
    @ApiResponse(responseCode = "404", description = "Conversation not found")
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.sendMessage(request, user.getDomainUser())
        );
    }

    // ============================
    // GET CONVERSATIONS
    // ============================
    @Operation(summary = "Get all conversations for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Conversations retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "403", description = "Unauthorized to view conversations")
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
    @Operation(summary = "Get details of a specific conversation")
    @ApiResponse(responseCode = "200", description = "Conversation details retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid conversation ID")
    @ApiResponse(responseCode = "403", description = "Unauthorized to view this conversation")
    @ApiResponse(responseCode = "404", description = "Conversation not found")
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        return ResponseEntity.ok(
                chatService.getConversation(conversationId, user.getDomainUser())
        );
    }
}