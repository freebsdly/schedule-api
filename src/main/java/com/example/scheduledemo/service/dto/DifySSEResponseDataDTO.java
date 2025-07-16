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
public class DifySSEResponseDataDTO {

    private UUID id;
    private UUID workflowID;
    private String status;
    private DifySSEResponseOutputsDTO outputs;
    private String error;
    private double elapsedTime;
    private long totalTokens;
    private long totalSteps;
    private long createdAt;
    private long finishedAt;
    private long exceptionsCount;
}
