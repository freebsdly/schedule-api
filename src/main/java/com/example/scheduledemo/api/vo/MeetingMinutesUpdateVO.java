package com.example.scheduledemo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会议记录修改参数")
public class MeetingMinutesUpdateVO {


    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    Long id;

    @Schema(description = "会议纪要内容", requiredMode = Schema.RequiredMode.REQUIRED)
    String rawData;
}
