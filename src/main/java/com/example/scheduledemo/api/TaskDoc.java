package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Task API")
public interface TaskDoc
{

    @Operation(summary = "Sync departments", description = "从天讯全量同步部门信息")
    APIResultVO<Void> syncDepartments() throws Exception;

    @Operation(summary = "Sync employees", description = "从天讯全量同步人员信息")
    APIResultVO<Void> syncEmployees() throws Exception;
}
