package com.example.scheduledemo.api;

import com.example.scheduledemo.entity.MeetingMinutesDto;
import com.example.scheduledemo.service.MeetingMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-minutes")
public class MeetingMinutesApi implements MeetingMinutesDoc {

    @Autowired
    private MeetingMinutesService meetingMinutesService;

    @GetMapping("/{id}")
    @Override
    public MeetingMinutesDto getMeetingMinutesById(@PathVariable Long id) {
        MeetingMinutesDto meetingMinutesById = meetingMinutesService.getMeetingMinutesById(id);
        return meetingMinutesById;
    }

    @GetMapping("")
    @Override
    public List<MeetingMinutesDto> getMeetingMinutes() {
        List<MeetingMinutesDto> meetingMinutes = meetingMinutesService.getMeetingMinutes();
        return meetingMinutes;
    }

    @PostMapping("")
    @Override
    public MeetingMinutesDto createMeetingMinutes(MeetingMinutesDto meetingMinutesDto) {
        MeetingMinutesDto meetingMinutes = meetingMinutesService.createMeetingMinutes(meetingMinutesDto);
        return meetingMinutes;
    }

    @PutMapping("")
    @Override
    public MeetingMinutesDto updateMeetingMinutes(MeetingMinutesDto meetingMinutesDto) {
        MeetingMinutesDto meetingMinutes = meetingMinutesService.updateMeetingMinutes(meetingMinutesDto);
        return meetingMinutes;
    }

    @DeleteMapping("/{id}")
    @Override
    public void deleteMeetingMinutes(@PathVariable Long id) {
        meetingMinutesService.deleteMeetingMinutes(id);
    }
}
