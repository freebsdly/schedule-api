package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Schedule Event API")
public interface ScheduleEventDoc {

    @Operation(summary = "Push Event Data to Table", description = "推送数据到多维表")
    APIResultVO<String> pushEventDataToTable(String nodeId, String content) throws Exception;

    @Operation(summary = "Generate Event Meeting Minutes", description = "输入event id, 从预定的用户和日历上查询日程录制音频转文字，并通过AI服务总结会议纪要，创建天讯文档，并更新多维表")
    APIResultVO<String> generateEventMeetingMinutes(String eventId) throws Exception;


    @Operation(summary = "Query Schedule Event")
    APIResultVO<List<ScheduleEventDTO>> getScheduleEvent();

}
