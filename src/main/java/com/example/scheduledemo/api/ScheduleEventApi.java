package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.EventVO;
import com.example.scheduledemo.api.vo.VOMapper;
import com.example.scheduledemo.service.DingTalkService;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.EventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule-events")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ScheduleEventApi implements ScheduleEventDoc {

    private final DingTalkService dingTalkService;

    private final ScheduleEventService scheduleEventService;

    @Override
    @PostMapping(value = "/push-data")
    public APIResultVO<String> pushEventDataToTable(@RequestParam String nodeId, @RequestBody String content)
            throws Exception {
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
    @PostMapping
    public APIResultVO<EventDTO.Detail> createScheduleEvent(@RequestBody EventVO.Create vo) throws Exception {
        EventDTO.Create dto = VOMapper.INSTANCE.toDTO(vo);
        EventDTO.Detail scheduleEvent = scheduleEventService.createScheduleEvent(dto);
        return APIResultVO.success(scheduleEvent);
    }

    @Override
    @PutMapping
    public APIResultVO<Void> updateScheduleEvent(@RequestBody EventVO.Update vo) throws Exception {
        EventDTO.Update dto = VOMapper.INSTANCE.toDTO(vo);
        scheduleEventService.updateScheduleEvent(dto);
        return APIResultVO.success();
    }

    @Override
    @DeleteMapping
    public APIResultVO<Void> deleteScheduleEvent(@ModelAttribute EventVO.Delete vo) throws Exception {
        EventDTO.Delete dto = VOMapper.INSTANCE.toDTO(vo);
        scheduleEventService.deleteScheduleEvent(dto);
        return APIResultVO.success();
    }

    @Override
    @GetMapping
    public APIResultVO<List<EventDTO.Detail>> queryScheduleEvents(@ModelAttribute EventVO.Query vo) {
        EventDTO.Query dto = VOMapper.INSTANCE.toDTO(vo);
        List<EventDTO.Detail> scheduleEvents = scheduleEventService.getScheduleEvents(dto);
        return APIResultVO.success(scheduleEvents);
    }
}
