package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Message;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySender_IdOrRecipient_Id(UUID senderId, UUID recipientId, Pageable pageable);
}