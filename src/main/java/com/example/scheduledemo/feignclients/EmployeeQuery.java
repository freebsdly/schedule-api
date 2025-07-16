package com.example.scheduledemo.feignclients;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeQuery {

    @JsonProperty("dept_id")
    Long departmentId;

    Long cursor;

    Integer size;
}
