package com.nc.horseretail.service;

import com.nc.horseretail.dto.ChatRequest;
import com.nc.horseretail.dto.ChatResponse;
import com.nc.horseretail.model.user.User;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    // Para enviar un mensaje nuevo
    ChatResponse sendMessage(ChatRequest request, User sender);

    // Para obtener el historial de una conversación
    List<ChatResponse> getHistory(UUID conversationId, User currentUser);
}