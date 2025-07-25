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
public class EmployeeResponseDTO
{

    @JsonProperty("request_id")
    String requestId;

    @JsonProperty("errcode")
    long errorCode;

    @JsonProperty("errmsg")
    String errorMessage;

    EmployeeResultDTO result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeResultDTO
    {

        @JsonProperty("has_more")
        boolean HasMore;

        @JsonProperty("next_cursor")
        long nextCursor;

        @JsonProperty("list")
        List<EmployeeInfoDTO> employees;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmployeeInfoDTO
    {

        @JsonProperty("userid")
        String userId;

        @JsonProperty("unionid")
        String unionId;

        String name;

        String email;
    }
}
