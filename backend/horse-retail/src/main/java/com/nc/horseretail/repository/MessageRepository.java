package com.nc.horseretail.repository;

import com.nc.horseretail.model.communication.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    // Trae todos los mensajes de una conversación ordenados por fecha
    List<Message> findByConversationIdOrderBySentAtAsc(UUID conversationId);
}