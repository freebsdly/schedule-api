package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "Schedule Event API")
public interface ScheduleEventDoc {

    @Operation(summary = "Get Event Cloud Record Text")
    APIResultVO<RecordTextResultDTO> getEventCloudRecordText(@ParameterObject QueryTextVO vo) throws Exception;

    @Operation(summary = "Get Event Cloud Record All Text")
    APIResultVO<String> pushEventDataToTable(String nodeId, String content) throws Exception;

    @Operation(summary = "Generate Event Meeting Minutes")
    APIResultVO<String> generateEventMeetingMinutes(String eventId) throws Exception;
}
