package com.example.scheduledemo.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DifyRequestBodyDTO {

    Map<String, Object> inputs;
    String query;
    @JsonProperty("response_mode")
    String responseMode;
    String user;
}
