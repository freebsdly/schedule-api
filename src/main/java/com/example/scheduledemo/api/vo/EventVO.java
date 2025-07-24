package com.example.scheduledemo.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventVO
{

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Schema(description = "操作日程参数")
    public static class Operate extends Common
    {

        @Schema(description = "操作类型", example = "create", allowableValues = {"create", "update", "delete"})
        private String action;

        @Schema(description = "日程ID", example = "1")
        private String id;

        @Schema(description = "钉钉日程ID", example = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
        private String dingTalkEventId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Schema(description = "创建日程参数")
    public static class Create extends Common
    {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Schema(description = "更新日程参数")
    public static class Update extends Common
    {
        @Schema(description = "日程ID，其与钉钉日程ID必须输入1个", example = "1")
        private Long id;

        @Schema(description = "钉钉日程ID，其与日程ID必须输入1个", example = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
        private String dingTalkEventId;
    }

    @Data
    @NoArgsConstructor
    @Schema(description = "查询日程参数")
    public static class Query
    {

        @Schema(description = "日程ID", example = "1")
        private Long id;

        @Schema(description = "钉钉日程ID", example = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
        private String dingTalkEventId;
    }

    @Data
    @NoArgsConstructor
    @Schema(description = "删除日程参数")
    public static class Delete
    {

        @Schema(description = "日程ID，其与钉钉日程ID必须输入1个", example = "1")
        private Long id;

        @Schema(description = "钉钉日程ID，其与日程ID必须输入1个", example = "TTFNZEdSaktVMzFOUDVISFFybnRYQT09")
        private String dingTalkEventId;
    }

    @Schema(description = "日程公共参数")
    @Data
    private static class Common
    {
        @Schema(description = "日程名称", example = "xxxx拜访沟通", requiredMode = Schema.RequiredMode.REQUIRED)
        private String summary;

        @Schema(description = "日程描述", example = "xxxx拜访沟通yyyy技术问题")
        private String description;

        @Schema(description = "开始时间", example = "2025-07-17 00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("start")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        LocalDateTime startTime;

        @Schema(description = "结束时间", example = "2025-07-17 01:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("end")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        LocalDateTime endTime;

        @Schema(description = "会议地点", example = "北京")
        private String location;

        @Schema(description = "是否全天会议", example = "true")
        @JsonProperty("is_all_day")
        private boolean isAllDay;

        @Schema(description = "重要性", example = "高", allowableValues = {"高", "低"}, requiredMode = Schema.RequiredMode.REQUIRED)
        private String importance;

        @Schema(description = "优先级", example = "高", allowableValues = {"高", "低"}, requiredMode = Schema.RequiredMode.REQUIRED)
        private String priority;

        @Schema(description = "日程组织人", requiredMode = Schema.RequiredMode.REQUIRED)
        private IdVO organizer;

        @Schema(description = "参会人列表", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private List<IdVO> attendees;

        @Schema(description = "日程拥有者人", requiredMode = Schema.RequiredMode.REQUIRED)
        private IdVO owner;
    }
}
