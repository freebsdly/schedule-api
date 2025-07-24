package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.EventVO;
import com.example.scheduledemo.api.vo.VOMapper;
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
public class ScheduleEventApi implements ScheduleEventDoc
{

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private ScheduleEventService scheduleEventService;

    @Override
    @PostMapping(value = "/push-data")
    public APIResultVO<String> pushEventDataToTable(@RequestParam String nodeId, @RequestBody String content)
            throws Exception
    {
        String result = dingTalkService.pushEventToMultiDimensionalTable(nodeId, content);
        return APIResultVO.success(result);
    }

    @Override
    @PostMapping(value = "/generate")
    public APIResultVO<String> generateEventMeetingMinutes(@RequestParam String eventId) throws Exception
    {
        String s = scheduleEventService.generateEventMeetingMinutes(eventId);
        return APIResultVO.success(s);
    }

    @Override
    @PostMapping
    public APIResultVO<ScheduleEventDTO> createScheduleEvent(@RequestBody EventVO.Create vo) throws Exception
    {
        ScheduleEventDTO dto = VOMapper.INSTANCE.toDTO(vo);
        ScheduleEventDTO scheduleEventDTO = scheduleEventService.createScheduleEvent(dto);
        return APIResultVO.success(scheduleEventDTO);
    }

    @Override
    @PutMapping
    public APIResultVO<Void> updateScheduleEvent(EventVO.Update vo) throws Exception
    {
        ScheduleEventDTO dto = VOMapper.INSTANCE.toDTO(vo);
        scheduleEventService.updateScheduleEvent(dto);
        return APIResultVO.success();
    }

    @Override
    @DeleteMapping
    public APIResultVO<String> deleteScheduleEvent(@ModelAttribute EventVO.Delete vo) throws Exception
    {
        if (vo.getId() != null && vo.getId() > 0) {
            scheduleEventService.deleteScheduleEvent(vo.getId());
        } else if (vo.getDingTalkEventId() != null && !vo.getDingTalkEventId().isEmpty()) {
            scheduleEventService.deleteScheduleEventByDingTalkId(vo.getDingTalkEventId());
        } else {
            return APIResultVO.failure("参数错误");
        }
        return APIResultVO.success("删除成功");
    }

    @Override
    @GetMapping
    public APIResultVO<List<ScheduleEventDTO>> queryScheduleEvents(EventVO.Query vo)
    {

        return null;
    }
}
