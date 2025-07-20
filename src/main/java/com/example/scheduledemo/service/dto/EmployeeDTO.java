package com.example.scheduledemo.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.example.scheduledemo.repository.entity.EmployeeEntity}
 */
@Data
public class EmployeeDTO implements Serializable
{
    Long id;
    String name;
    String email;
    String badge;
    String unionId;
    DepartmentDTO department;
}