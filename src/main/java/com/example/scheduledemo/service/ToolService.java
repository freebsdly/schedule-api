package com.example.scheduledemo.service;

import com.example.scheduledemo.service.dto.DifyBlockResponseDTO;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToolService {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private AIService aiService;


    @Tool(description = "generate Meeting Minutes")
    public String generateMeetingMinutes(
            @ToolParam(description = "user union id")
            String unionId,
            @ToolParam(description = "calendarId")
            String calendarId,
            @ToolParam(description = "event id")
            String eventId) throws Exception {
        RecordTextResultDTO info = dingTalkService.getEventCloudRecordAllText(unionId, calendarId, eventId);
        DifyBlockResponseDTO result = aiService.getMeetingMinutes(info.getText());
        return result.getAnswer();
    }
}
