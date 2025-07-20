package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntentRecognitionDTO
{

    private String intent;

    private String output;

    private ScheduleEventDTO slots;
}
