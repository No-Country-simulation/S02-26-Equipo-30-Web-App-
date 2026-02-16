package com.nc.horseretail.controller;

import com.nc.horseretail.config.SecurityUser;
import com.nc.horseretail.dto.ChatRequest;
import com.nc.horseretail.dto.ChatResponse;
import com.nc.horseretail.service.ChatService;
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

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request,
                                                    @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(chatService.sendMessage(request, securityUser.getDomainUser()));
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<ChatResponse>> getHistory(@PathVariable UUID conversationId,
                                                         @AuthenticationPrincipal SecurityUser securityUser) {

        return ResponseEntity.ok(chatService.getHistory(conversationId, securityUser.getDomainUser()));
    }
}