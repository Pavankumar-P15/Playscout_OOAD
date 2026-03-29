package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

    @JsonProperty("_id")
    private UUID id;

    private MessageUserResponse senderId;
    private MessageUserResponse recipientId;
    private String content;
    private LocalDateTime timestamp;
}