package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.EventVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Web Hook API")
public interface WebHookDoc
{

    @Operation(summary = "Operate Schedule Event", description = "操作日程, 提供给多维表管理日程使用(dingtalk webhook)")
    APIResultVO<Object> operateScheduleEvent(EventVO.Operate vo) throws Exception;
}
