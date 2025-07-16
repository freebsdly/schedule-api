package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import com.example.scheduledemo.service.DingTalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule-events")
public class ScheduleEventApi implements ScheduleEventDoc {

    @Autowired
    private DingTalkService dingTalkService;

    @Override
    @GetMapping(value = "/text")
    public APIResultVO<String> getEventCloudRecordText(@ModelAttribute QueryTextVO vo) throws Exception {
        String text = dingTalkService.getEventCloudRecordAllText(vo.getUnionId(), vo.getCalendarId(), vo.getEventId());
        return APIResultVO.success(text);
    }
}
