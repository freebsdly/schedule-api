package com.example.scheduledemo.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushDataDTO {
    private String id;
    private String action;
    private String summary;
    private String description;
    private List<String> keywords;
    private String status;
    @JsonProperty("start")
    private String startTime;
    @JsonProperty("end")
    private String endTime;
    private String location;
    @JsonProperty("is_all_day")
    private boolean isAllDay;
    private String importance;
    private String priority;
    private IdDTO organizer;
    private List<IdDTO> attendees;

    @JsonProperty("meeting_minutes_url")
    private String meetingMinutesUrl;
}
