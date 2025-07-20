package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.MeetingMinutesAddVO;
import com.example.scheduledemo.api.vo.MeetingMinutesUpdateVO;
import com.example.scheduledemo.api.vo.MeetingMinutesVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Meeting Minutes API")
public interface MeetingMinutesDoc
{

    @Operation(summary = "Query meeting minutes")
    APIResultVO<List<MeetingMinutesVO>> getMeetingMinutes();

    @Operation(summary = "Create a new meeting minutes")
    APIResultVO<MeetingMinutesVO> createMeetingMinutes(MeetingMinutesAddVO vo);

    @Operation(summary = "Update a meeting minutes")
    APIResultVO<MeetingMinutesVO> updateMeetingMinutes(MeetingMinutesUpdateVO vo);

    @Operation(summary = "Delete a meeting minutes")
    APIResultVO<Long> deleteMeetingMinutes(Long id);
}
