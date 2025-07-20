package com.example.scheduledemo.service.dto;

import com.example.scheduledemo.repository.entity.MeetingMinutesEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link MeetingMinutesEntity}
 */
@Data
public class MeetingMinutesDTO implements Serializable
{
    Long id;
    String rawData;
    String answer;
    List<ActionItemDTO> actionItems;
    ScheduleEventDTO event;
    String meetingMinutesUrl;
}