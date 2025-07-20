package com.example.scheduledemo.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.example.scheduledemo.repository.entity.EventAttendeeEntity}
 */
@Data
@NoArgsConstructor
public class EventAttendeeDTO implements Serializable
{
    Long id;
    Long employeeId;
    String employeeUnionId;
    Long eventId;
    String employeeName;
}