package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.EventVO;
import com.example.scheduledemo.api.vo.VOMapper;
import com.example.scheduledemo.service.ScheduleEventService;
import com.example.scheduledemo.service.dto.EventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WebHookApi implements WebHookDoc
{
    private final ScheduleEventService scheduleEventService;

    @PostMapping(value = "")
    public APIResultVO<Object> operateScheduleEvent(@RequestBody EventVO.Operate vo) throws Exception
    {
        log.debug("operate event: {}", vo);
        switch (vo.getAction()) {
            case "create" -> {
                EventDTO.Create dto = VOMapper.INSTANCE.toCreateDTO(vo);
                EventDTO.Detail scheduleEvent = scheduleEventService.createScheduleEvent(dto);
                return APIResultVO.success(scheduleEvent);
            }
            case "update" -> {
                EventDTO.Update dto = VOMapper.INSTANCE.toUpdateDTO(vo);
                EventDTO.Detail detail = scheduleEventService.updateScheduleEvent(dto);
                return APIResultVO.success(detail);
            }
            case "delete" -> {
                EventDTO.Delete dto = VOMapper.INSTANCE.toDeleteDTO(vo);
                scheduleEventService.deleteScheduleEvent(dto);
                return APIResultVO.success();
            }
            default -> {
                return APIResultVO.failure("不支持的操作");
            }
        }
    }
}
