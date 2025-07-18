package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.service.DingTalkService;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule-events")
public class ScheduleEventApi implements ScheduleEventDoc {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private ScheduleEventService scheduleEventService;

    @Override
    @PostMapping(value = "/push-data")
    public APIResultVO<String> pushEventDataToTable(@RequestParam String nodeId, @RequestBody String content) throws Exception {
        String result = dingTalkService.pushEventToMultiDimensionalTable(nodeId, content);
        return APIResultVO.success(result);
    }

    @Override
    @PostMapping(value = "/generate")
    public APIResultVO<String> generateEventMeetingMinutes(@RequestParam String eventId) throws Exception {
        String s = scheduleEventService.generateEventMeetingMinutes(eventId);
        return APIResultVO.success(s);
    }

    @Override
    public APIResultVO<List<ScheduleEventDTO>> getScheduleEvent() {
        List<ScheduleEventDTO> scheduleEvents = scheduleEventService.getScheduleEvents();
        return APIResultVO.success(scheduleEvents);
    }
}
