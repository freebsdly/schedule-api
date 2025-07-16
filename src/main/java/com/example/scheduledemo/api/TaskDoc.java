package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Task API")
public interface TaskDoc {

    @Operation(summary = "Sync departments")
    APIResultVO<Void> syncDepartments() throws Exception;

    @Operation(summary = "Sync employees")
    APIResultVO<Void> syncEmployees() throws Exception;
}
