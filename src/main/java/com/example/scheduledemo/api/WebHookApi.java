package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.EventVO;
import com.example.scheduledemo.api.vo.VOMapper;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
@Slf4j
public class WebHookApi implements WebHookDoc
{

    @Autowired
    private ScheduleEventService scheduleEventService;

    @PostMapping(value = "")
    public APIResultVO<Object> operateScheduleEvent(@RequestBody EventVO.Operate vo) throws Exception
    {
        log.debug("operate event: {}", vo);
        ScheduleEventDTO dto = VOMapper.INSTANCE.toDTO(vo);
        if ("".equals(vo.getId()) || vo.getId() == null) {
            ScheduleEventDTO scheduleEventDTO = scheduleEventService.createScheduleEvent(dto);
            return APIResultVO.success(scheduleEventDTO);
        } else {
            ScheduleEventDTO update = scheduleEventService.updateScheduleEvent(dto);
            return APIResultVO.success(update);
        }
        // TODO: 处理删除
    }
}
