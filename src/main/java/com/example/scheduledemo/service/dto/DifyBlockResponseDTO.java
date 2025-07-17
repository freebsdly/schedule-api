package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyBlockResponseDTO {
    private String event;
    private UUID taskID;
    private UUID id;
    private UUID messageID;
    private UUID conversationID;
    private String mode;
    private String answer;
    private long createdAt;
}
