package com.nc.horseretail.repository;

import com.nc.horseretail.model.communication.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT m
        FROM Message m
        JOIN FETCH m.sender
        WHERE m.conversation.id = :conversationId
        ORDER BY m.sentAt ASC
    """)
    List<Message> findConversationMessages(UUID conversationId);

    Optional<Message> findTopByConversationIdOrderBySentAtDesc(UUID conversationId);
}