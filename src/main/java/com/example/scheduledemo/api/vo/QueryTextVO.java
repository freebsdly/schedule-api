package com.example.scheduledemo.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询日程关联会议语音转文本参数")
public class QueryTextVO {
    @Schema(description = "用户企业内部ID", defaultValue = "BngfsiSEsS46PchVXXtPTQgiEiE")
    String unionId;
    @Schema(description = "用户日历ID", defaultValue = "primary")
    String calendarId;
    @Schema(description = "用户日程ID", defaultValue = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
    String eventId;
}
