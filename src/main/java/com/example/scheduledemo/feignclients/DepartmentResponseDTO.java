package com.example.scheduledemo.feignclients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDTO {

    @JsonProperty("request_id")
    String requestId;

    @JsonProperty("errcode")
    long errorCode;

    @JsonProperty("errmsg")
    String errorMessage;

    List<DeptBaseResponseDTO> result;
}
