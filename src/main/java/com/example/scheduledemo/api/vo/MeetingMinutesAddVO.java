package com.example.scheduledemo.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


@Schema(description = "会议记录新增参数")
@Data
public class MeetingMinutesAddVO implements Serializable {

    @Schema(description = "会议纪要内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("raw_data")
    String rawData;
}
