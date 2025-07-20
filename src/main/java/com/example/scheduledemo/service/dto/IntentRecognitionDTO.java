package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntentRecognitionDTO
{

    private String intent;

    private String output;

    private String summary;

    private String description;

    private String location;

    private String startTime;

    private String endTime;

    private List<String> attendees;
}
