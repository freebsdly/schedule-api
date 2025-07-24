package com.example.scheduledemo.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class EventDTO {

    @EqualsAndHashCode(callSuper = true)
    @Data
    @NoArgsConstructor
    public static class Create extends Common
    {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update extends Common
    {

        Long id;
        String dingtalkEventId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Delete
    {

        Long id;
        String dingtalkEventId;
    }

    public static class Query extends Common
    {

        Long id;
        String dingtalkEventId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail extends Common
    {

        Long id;
        String dingtalkEventId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Common implements Serializable
    {

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
        EmployeeDTO owner;
    }
}
