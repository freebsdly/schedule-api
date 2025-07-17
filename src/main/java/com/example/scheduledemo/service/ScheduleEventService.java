package com.example.scheduledemo.service;

import com.example.scheduledemo.service.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScheduleEventService {

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private MeetingMinutesService meetingMinutesService;

    @Autowired
    private AIService aiService;

    @Autowired
    private ObjectMapper objectMapper;


    // TODO: 放到IOC容器中
    @Value("${schedule-event.user.union-id}")
    private String userUnionId;

    @Value("${schedule-event.user.calendar-id}")
    private String userCalendarId;

    @Value("${schedule-event.user.event-table-id}")
    private String userEventTableId;

    @Value("${schedule-event.user.action-item-table-id}")
    private String userActionItemTableId;

    @Value("${schedule-event.user.kb.workspace-id}")
    private String userKbWorkspaceId;

    @Value("${schedule-event.user.kb.node-id}")
    private String userKbNodeId;

    @Value("${schedule-event.user.push-keywords}")
    private List<String> pushKeywords;

    @Value("${schedule-event.user.meeting-minutes.template-name}")
    private String userMeetingMinutesTemplateName;

    @Autowired
    private SpringTemplateEngine templateEngine;


    private String getDate(String utcDateTime) {
        Instant parse = Instant.parse(utcDateTime);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(parse, ZoneId.of("Asia/Shanghai"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }

    private String processMarkdown(String templateName, String content) throws JsonProcessingException {
        Map<String, Object> variables = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {
        });
        Context context = new Context();
        context.setVariables(variables);
        return templateEngine.process(templateName, context);
    }

    public String generateEventMeetingMinutes(String eventId) throws Exception {
        RecordTextResultDTO info = dingTalkService.getEventCloudRecordAllText(userUnionId, userCalendarId, eventId);
        DifyBlockResponseDTO meetingMinutes = aiService.getMeetingMinutes(info.getText());
        String startDate = getDate(info.getStartTime());
        String answer = meetingMinutes.getAnswer();
        try {
            answer = processMarkdown(userMeetingMinutesTemplateName, meetingMinutes.getAnswer());
            log.debug("meeting minutes: {}", answer);
        } catch (Exception e) {
            log.warn("process markdown error: {}", e.getMessage());
        }
        String docName = String.format("%s Meeting Minutes - %s", startDate, info.getSummary());
        CreateDocDTO docDTO = new CreateDocDTO();
        docDTO.setWorkspaceId(userKbWorkspaceId);
        docDTO.setUnionId(userUnionId);
        docDTO.setParentId(userKbNodeId);
        docDTO.setDocName(docName);
        docDTO.setContent(answer);
        docDTO.setDocType("DOC");
        docDTO.setContentType("markdown");
        CreateDocResultDTO kbDoc = dingTalkService.createKBDoc(docDTO);
        // TODO: 推送到多维表, 从事件中获取更多信息
        PushDataDTO pushDataDTO = new PushDataDTO();
        pushDataDTO.setAction("add");
        pushDataDTO.setSummary(info.getSummary());
        pushDataDTO.setKeywords(pushKeywords);
        String content = objectMapper.writeValueAsString(pushDataDTO);
        dingTalkService.pushEventToMultiDimensionalTable(userEventTableId, content);

        return kbDoc.getDocUrl();
    }
}
