package com.example.scheduledemo.api;

import com.example.scheduledemo.entity.MeetingMinutesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Meeting Minutes API")
public interface MeetingMinutesDoc {

    @Operation(summary = "Get meeting minutes by id")
    MeetingMinutesDto getMeetingMinutesById(Long id);

    @Operation(summary = "Get all meeting minutes")
    List<MeetingMinutesDto> getMeetingMinutes();

    @Operation(summary = "Create a new meeting minutes")
    MeetingMinutesDto createMeetingMinutes(MeetingMinutesDto meetingMinutesDto);

    @Operation(summary = "Update a meeting minutes")
    MeetingMinutesDto updateMeetingMinutes(MeetingMinutesDto meetingMinutesDto);

    @Operation(summary = "Delete a meeting minutes")
    void deleteMeetingMinutes(Long id);
}
