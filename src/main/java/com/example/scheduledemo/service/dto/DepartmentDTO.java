package com.example.scheduledemo.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.example.scheduledemo.repository.entity.DepartmentEntity}
 */
@Data
public class DepartmentDTO implements Serializable
{
    Long id;
    String name;
    Long dingTalkDepartmentId;
    Long dingTalkParentId;
}