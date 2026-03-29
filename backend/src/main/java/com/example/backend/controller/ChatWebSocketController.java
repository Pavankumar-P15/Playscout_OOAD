package com.example.backend.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.backend.dto.MessageResponse;
import com.example.backend.dto.SendMessageRequest;
import com.example.backend.service.MessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequest request, Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Unauthorized websocket session");
        }

        MessageResponse saved = messageService.sendMessage(principal.getName(), request);

        String recipientQueue = "/queue/messages." + saved.getRecipientId().getId();
        String senderQueue = "/queue/messages." + saved.getSenderId().getId();

        messagingTemplate.convertAndSend(recipientQueue, saved);
        messagingTemplate.convertAndSend(senderQueue, saved);
    }
}