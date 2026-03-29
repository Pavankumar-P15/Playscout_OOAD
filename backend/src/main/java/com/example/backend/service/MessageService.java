package com.example.backend.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.MessageUserResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.model.Message;
import com.example.backend.model.User;
import com.example.backend.repository.MessageRepository;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final int MAX_HISTORY_MESSAGES = 200;
    private static final int MAX_CONTENT_LENGTH = 1000;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public List<MessageResponse> getMessagesForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Message> recentMessages = messageRepository.findBySender_IdOrRecipient_Id(
                user.getId(),
                user.getId(),
                PageRequest.of(0, MAX_HISTORY_MESSAGES, Sort.by(Sort.Direction.DESC, "createdAt")));

        Collections.reverse(recentMessages);

        return recentMessages.stream()
                .map(this::toResponse)
                .toList();
    }

    public MessageResponse sendMessage(String senderEmail, SendMessageRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getRecipientId() == null) {
            throw new IllegalArgumentException("Recipient is required");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }

        String content = request.getContent().trim();
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("Message content is too long");
        }

        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User recipient = userRepository.findById(request.getRecipientId())
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);

        Message saved = messageRepository.save(message);
        return toResponse(saved);
    }

    private MessageResponse toResponse(Message message) {
        MessageUserResponse sender = new MessageUserResponse(
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSender().getUserImage());

        MessageUserResponse recipient = new MessageUserResponse(
                message.getRecipient().getId(),
                message.getRecipient().getName(),
                message.getRecipient().getUserImage());

        return new MessageResponse(
                message.getId(),
                sender,
                recipient,
                message.getContent(),
                message.getCreatedAt());
    }
}