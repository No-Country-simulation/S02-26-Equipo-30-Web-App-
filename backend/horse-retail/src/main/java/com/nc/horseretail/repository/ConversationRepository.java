package com.nc.horseretail.repository;

import com.nc.horseretail.model.communication.Conversation;
import com.nc.horseretail.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    Optional<Conversation> findByListingIdAndStartedById(UUID listingId, UUID startedById);

    @Query("""
        SELECT c
        FROM Conversation c
        JOIN FETCH c.listing l
        JOIN FETCH l.owner
        JOIN FETCH c.startedBy
        WHERE c.startedBy.id = :userId
           OR l.owner.id = :userId
        ORDER BY c.updatedAt DESC
    """)
    List<Conversation> findUserConversations(UUID userId);
}