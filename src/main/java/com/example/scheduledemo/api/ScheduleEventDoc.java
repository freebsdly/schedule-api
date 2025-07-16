package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "Schedule Event API")
public interface ScheduleEventDoc {

    @Operation(summary = "Get Event Cloud Record Text")
    APIResultVO<String> getEventCloudRecordText(@ParameterObject QueryTextVO vo) throws Exception;
}
