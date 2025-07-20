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
public class DifySSEResponseDTO
{

    private String event;
    private UUID conversationID;
    private UUID messageID;
    private long createdAt;
    private UUID taskID;
    private UUID workflowRunID;
    private SSEResponseData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SSEResponseData
    {

        private UUID id;
        private UUID workflowID;
        private String status;
        private SSEResponseOutputs outputs;
        private String error;
        private double elapsedTime;
        private long totalTokens;
        private long totalSteps;
        private long createdAt;
        private long finishedAt;
        private long exceptionsCount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SSEResponseOutputs
    {
        private String answer;
    }
}
