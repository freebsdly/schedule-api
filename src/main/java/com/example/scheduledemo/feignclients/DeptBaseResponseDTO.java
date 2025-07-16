package com.example.scheduledemo.feignclients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeptBaseResponseDTO {

    @JsonProperty("dept_id")
    Long departmentId;

    String name;

    @JsonProperty("parent_id")
    Long parentId;

    String status = "0";
}
