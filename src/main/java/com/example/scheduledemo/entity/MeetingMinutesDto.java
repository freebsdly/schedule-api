package com.example.scheduledemo.entity;

import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link MeetingMinutesEntity}
 */
@Value
public class MeetingMinutesDto implements Serializable {
    Long id;
    String rawData;

}