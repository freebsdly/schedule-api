package com.example.scheduledemo.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link com.example.scheduledemo.repository.entity.ScheduleEventEntity}
 */
@Data
public class ScheduleEventDTO implements Serializable {
    Long id;
    String dingtalkEventId;
    String summary;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String location;
    EmployeeDTO organizer;
    List<EventAttendeeDTO> attendees;
    Boolean isAllDay;
    String importance;
    String priority;
    String status;
}