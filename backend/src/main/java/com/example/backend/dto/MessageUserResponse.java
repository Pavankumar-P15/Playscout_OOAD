package com.example.backend.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageUserResponse {

    @JsonProperty("_id")
    private UUID id;

    private String name;
    private String userImage;
}