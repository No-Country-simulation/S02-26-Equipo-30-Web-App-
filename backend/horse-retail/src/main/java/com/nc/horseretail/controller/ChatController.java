package com.nc.horseretail.controller;

import com.nc.horseretail.dto.communication.ChatRequest;
import com.nc.horseretail.dto.communication.ChatResponse;
import com.nc.horseretail.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<List<ChatResponse>> getHistory(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(chatService.getHistory(conversationId));
    }
}