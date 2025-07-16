package com.example.scheduledemo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link MeetingMinutesEntity}
 */
@Data
public class MeetingMinutesDTO implements Serializable {
    Long id;
    String rawData;

}