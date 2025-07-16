package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DifyMeetingMinutesDTO {

    private String[] agenda;
    private String[] meetingResolution;
    private DifyActionItemDTO[] actionItems;
    private String[] otherTopics;
}


