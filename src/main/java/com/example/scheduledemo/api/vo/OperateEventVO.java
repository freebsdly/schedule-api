package com.example.scheduledemo.api.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "操作日程参数")
public class OperateEventVO {

    @Schema(description = "操作类型", example = "create", allowableValues = {"create", "update", "delete"})
    private String action;

    @Schema(description = "日程ID", example = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
    private String id;

    @Schema(description = "日程名称", example = "xxxx拜访沟通")
    private String summary;

    @Schema(description = "日程描述", example = "xxxx拜访沟通yyyy技术问题")
    private String description;

    @Schema(description = "开始时间", example = "2025-07-17 00:00:00")
    @JsonProperty("start")
    private String startTime;

    @Schema(description = "结束时间", example = "2025-07-17 01:00:00")
    @JsonProperty("end")
    private String endTime;

    @Schema(description = "会议地点", example = "北京")
    private String location;

    @Schema(description = "是否全天会议", example = "true")
    @JsonProperty("is_all_day")
    private boolean isAllDay;

    @Schema(description = "重要性", example = "高", allowableValues = {"高", "低"})
    private String importance;

    @Schema(description = "优先级", example = "高", allowableValues = {"高", "低"})
    private String priority;

    @Schema(description = "日程组织人", example = "")
    private IdVO organizer;

    @Schema(description = "参会人ID列表", example = "", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<IdVO> attendeeIds;
}
