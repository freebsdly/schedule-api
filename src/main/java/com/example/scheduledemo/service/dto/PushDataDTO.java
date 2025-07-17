package com.example.scheduledemo.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushDataDTO {

    private String action;
    private String summary;
    private List<String> keywords;
    private String meetingMinutesUrl;
}
