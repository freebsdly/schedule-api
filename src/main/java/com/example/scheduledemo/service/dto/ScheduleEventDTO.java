package com.example.scheduledemo.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime endTime;
    String location;
    EmployeeDTO organizer;
    List<EventAttendeeDTO> attendees;
    Boolean isAllDay;
    String importance;
    String priority;
    String status;
    String calendarId;

}