package com.example.scheduledemo.api.vo;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.scheduledemo.entity.MeetingMinutesDto}
 */
@Value
public class MeetingMinutesVO implements Serializable {
    Long id;
    String rawData;
}