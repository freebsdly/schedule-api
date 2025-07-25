package com.example.scheduledemo.service.dto;

import com.aliyun.dingtalkcalendar_1_0.models.GetEventResponseBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordTextResultDTO
{
    private String eventId;
    private String summary;
    private String text;
    private String startTime;
    private GetEventResponseBody event;
}
