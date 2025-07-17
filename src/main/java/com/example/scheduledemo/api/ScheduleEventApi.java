package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import com.example.scheduledemo.service.DingTalkService;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule-events")
public class ScheduleEventApi implements ScheduleEventDoc {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private ScheduleEventService scheduleEventService;

    @Override
    @GetMapping(value = "/text")
    public APIResultVO<RecordTextResultDTO> getEventCloudRecordText(@ModelAttribute QueryTextVO vo) throws Exception {
        RecordTextResultDTO info = dingTalkService.getEventCloudRecordAllText(vo.getUnionId(), vo.getCalendarId(), vo.getEventId());
        return APIResultVO.success(info);
    }

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
}
