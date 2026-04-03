package com.example.backend.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class GameResponse {

    public GameResponse(
        UUID id,
        LocalDate date,
        String slot,
        String sportIcon,
        String sportName,
        UUID createdBy,
        Integer membersJoined,
        Integer totalMembers,
        String skillLevel,
        String courtName,
        String location,
        String userName,
        String userImage
    ) {
        this.id = id;
        this.date = date;
        this.slot = slot;
        this.sportIcon = sportIcon;
        this.sportName = sportName;
        this.createdBy = createdBy;
        this.membersJoined = membersJoined;
        this.totalMembers = totalMembers;
        this.skillLevel = skillLevel;
        this.courtName = courtName;
        this.location = location;
        this.userName = userName;
        this.userImage = userImage;
    }
    
    @JsonProperty("_id")
    private final UUID id;

    private final LocalDate date;
    private final String slot;
    private final String sportIcon;
    private final String sportName;

    @JsonProperty("userID")
    private final UUID createdBy;
    
    private final Integer membersJoined;
    private final Integer totalMembers;

    @JsonProperty("level")
    private final String skillLevel;

    private final String courtName;
    private final String location;
    private final String userName;
    private final String userImage;
}
