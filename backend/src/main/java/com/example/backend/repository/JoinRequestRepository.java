package com.example.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.enums.JoinRequestStatus;
import com.example.backend.model.JoinRequest;

public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {
    List<JoinRequest> findByRecipientId_IdOrderByCreatedAtDesc(UUID currentUserId);

    List<JoinRequest> findByRecipientId_IdAndStatusOrderByCreatedAtDesc(UUID currentUserId, JoinRequestStatus status);

    List<JoinRequest> findBySenderId_IdOrderByCreatedAtDesc(UUID currentUserId);

    List<JoinRequest> findBySenderId_IdAndStatusOrderByCreatedAtDesc(UUID currentUserId, JoinRequestStatus status);

    Optional<JoinRequest> findByIdAndRecipientId_Id(UUID requestId, UUID currentUserId);

    Optional<JoinRequest> findByIdAndSenderId_Id(UUID requestId, UUID currentUserId);

    boolean existsBySenderId_IdAndGameId_IdAndStatus(UUID senderId, UUID gameId, JoinRequestStatus status);
    
    @Query("SELECT jr FROM JoinRequest jr WHERE jr.recipientId.id = :userId AND jr.gameId.date >= CURRENT_DATE ORDER BY jr.createdAt DESC")
    List<JoinRequest> findIncomingRequestsForUpcomingGames(@Param("userId") UUID userId);
    
    @Query("SELECT jr FROM JoinRequest jr WHERE jr.senderId.id = :userId AND jr.gameId.date >= CURRENT_DATE ORDER BY jr.createdAt DESC")
    List<JoinRequest> findSentRequestsForUpcomingGames(@Param("userId") UUID userId);
}
