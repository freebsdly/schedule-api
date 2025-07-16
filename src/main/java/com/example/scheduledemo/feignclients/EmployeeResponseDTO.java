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
public class EmployeeResponseDTO {

    @JsonProperty("request_id")
    String requestId;

    @JsonProperty("errcode")
    long errorCode;

    @JsonProperty("errmsg")
    String errorMessage;

    EmployeeResultDTO result;
}
