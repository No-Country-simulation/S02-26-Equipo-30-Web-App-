package com.nc.horseretail.repository;

import com.nc.horseretail.model.communication.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    // Busca si ya existe una conversación entre este usuario y este anuncio específico
    Optional<Conversation> findByListingIdAndStartedById(UUID listingId, UUID userId);
}