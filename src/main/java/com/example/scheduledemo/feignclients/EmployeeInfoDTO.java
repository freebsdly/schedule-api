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
public class EmployeeInfoDTO {

    @JsonProperty("userid")
    String userId;

    @JsonProperty("unionid")
    String unionId;

    String name;

    String email;
}
