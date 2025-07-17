package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.*;
import com.example.scheduledemo.service.dto.CreateDocDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import java.util.List;

@Tag(name = "Meeting Minutes API")
public interface MeetingMinutesDoc {

    @Operation(summary = "Query meeting minutes")
    APIResultVO<List<MeetingMinutesVO>> getMeetingMinutes();

    @Operation(summary = "Create a new meeting minutes")
    APIResultVO<MeetingMinutesVO> createMeetingMinutes(MeetingMinutesAddVO vo);

    @Operation(summary = "Update a meeting minutes")
    APIResultVO<MeetingMinutesVO> updateMeetingMinutes(MeetingMinutesUpdateVO vo);

    @Operation(summary = "Delete a meeting minutes")
    APIResultVO<Long> deleteMeetingMinutes(Long id);

    @Operation(summary = "Generate meeting minutes using AI")
    APIResultVO<String> generateMeetingMinutes(@ParameterObject GenerateMeetingMinutesVO vo) throws Exception;



}
