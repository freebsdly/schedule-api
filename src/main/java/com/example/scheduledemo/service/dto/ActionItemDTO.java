package com.example.scheduledemo.service.dto;

import com.example.scheduledemo.repository.entity.ActionItemEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link ActionItemEntity}
 */
@Data
public class ActionItemDTO implements Serializable
{
    Long id;
    String name;
    String description;
    String status;
    LocalDateTime dueDate;
}