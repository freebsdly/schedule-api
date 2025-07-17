package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.OperateEventVO;
import com.example.scheduledemo.api.vo.VOMapper;
import com.example.scheduledemo.service.DingTalkService;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping(value = "")
    public APIResultVO<Object> operateScheduleEvent(@RequestBody OperateEventVO vo) throws Exception {
        ScheduleEventDTO dto = VOMapper.INSTANCE.toDTO(vo);
        switch (vo.getAction()) {
            case "create":
                ScheduleEventDTO scheduleEventDTO = scheduleEventService.createScheduleEvent(dto);
                return APIResultVO.success(scheduleEventDTO);
            case "update":
                ScheduleEventDTO update = scheduleEventService.updateScheduleEvent(dto);
                return APIResultVO.success(update);
            case "delete":
                scheduleEventService.deleteScheduleEventByDingTalkId(vo.getId());
                return APIResultVO.success(null);
            default:
                return APIResultVO.failure(500L, "Invalid action");
        }
    }

    @Override
    public APIResultVO<List<ScheduleEventDTO>> getScheduleEvent() {
        List<ScheduleEventDTO> scheduleEvents = scheduleEventService.getScheduleEvents();
        return APIResultVO.success(scheduleEvents);
    }
}
