package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.APIResultVO;
import com.example.scheduledemo.api.vo.QueryTextVO;
import com.example.scheduledemo.api.vo.RobotSendMessageVO;
import com.example.scheduledemo.service.DingTalkService;
import com.example.scheduledemo.service.dto.CreateDocDTO;
import com.example.scheduledemo.service.dto.CreateDocResultDTO;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dingtalk")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DingTalkApi implements DingTalkDoc {

    private final DingTalkService dingTalkService;

    private final ObjectMapper objectMapper;

    @Override
    @GetMapping(value = "/calendars/events/cloud-records/text")
    public APIResultVO<RecordTextResultDTO> getEventCloudRecordText(@ModelAttribute QueryTextVO vo) throws Exception {
        RecordTextResultDTO info = dingTalkService.getEventCloudRecordAllText(
                vo.getUnionId(),
                vo.getCalendarId(),
                vo.getEventId());
        return APIResultVO.success(info);
    }

    @Override
    @PostMapping(value = "/kb/docs")
    public APIResultVO<String> createKnowledgeBaseDoc(@RequestBody CreateDocDTO dto) throws Exception {
        CreateDocResultDTO kbDoc = dingTalkService.createKBDoc(dto);
        return APIResultVO.success(kbDoc.getDocUrl());
    }

    @Override
    @PostMapping(value = "/robot/messages")
    public APIResultVO<Void> robotBatchSendMessage(@RequestBody RobotSendMessageVO vo) throws Exception {
        String params = objectMapper.writeValueAsString(vo.getMsgParam());
        dingTalkService.robotBatchSendMessage(vo.getUserIds(), vo.getMsgKey(), params);
        return APIResultVO.success(null);
    }
}
