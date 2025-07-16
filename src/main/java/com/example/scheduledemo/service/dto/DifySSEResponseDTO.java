package com.example.scheduledemo.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DifySSEResponseDTO {

    private String event;
    private UUID conversationID;
    private UUID messageID;
    private long createdAt;
    private UUID taskID;
    private UUID workflowRunID;
    private DifySSEResponseDataDTO data;
}
