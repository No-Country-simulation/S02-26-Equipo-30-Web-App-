package com.nc.horseretail.service;

import com.nc.horseretail.dto.communication.ChatRequest;
import com.nc.horseretail.dto.communication.ChatResponse;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    
    // Para enviar un mensaje nuevo
    ChatResponse sendMessage(ChatRequest request);
    
    // Para obtener el historial de una conversación
    List<ChatResponse> getHistory(UUID conversationId);
}