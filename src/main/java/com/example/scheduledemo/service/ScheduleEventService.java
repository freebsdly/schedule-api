package com.example.scheduledemo.service;

import com.aliyun.dingtalkcalendar_1_0.models.GetEventResponseBody;
import com.example.scheduledemo.exception.BusinessException;
import com.example.scheduledemo.repository.ScheduleEventRepository;
import com.example.scheduledemo.repository.entity.ScheduleEventEntity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ScheduleEventService {
    @Autowired
    private ScheduleEventRepository scheduleEventRepository;

    @Autowired
    private DingTalkService dingTalkService;

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

    private String processMarkdown(String templateName, Map<String, Object> content) throws JsonProcessingException {
        Context context = new Context();
        context.setVariables(content);
        return templateEngine.process(templateName, context);
    }

    private Map<String, Object> answerMapper(String answer, GetEventResponseBody event) throws Exception {
        Map<String, Object> variables = new HashMap<>();
        try {
            variables = objectMapper.readValue(answer, new TypeReference<Map<String, Object>>() {
            });
            variables.put("hasError", false);
        } catch (Exception ex) {
            log.warn("answer error: {}", ex.getMessage());
            // 填充空数据确保会议纪要markdown可以继续渲染
            variables.put("agenda", List.of());
            variables.put("meetingResolution", List.of());
            variables.put("actionItems", List.of());
            variables.put("otherTopics", List.of());
            variables.put("hasError", true);
        }

        variables.put("event", event);
        return variables;
    }

    public String generateEventMeetingMinutes(String eventId) throws Exception {
        RecordTextResultDTO info = dingTalkService.getEventCloudRecordAllText(userUnionId, userCalendarId, eventId);
        Map<String, Object> variables;
        String startDate = getDate(info.getStartTime());
        String answer = info.getText();
        if (!answer.isEmpty()) {
            DifyBlockResponseDTO meetingMinutes = aiService.getMeetingMinutes(answer);
            answer = meetingMinutes.getAnswer();
        }
        GetEventResponseBody event = info.getEvent();
        variables = answerMapper(answer, event);
        try {
            answer = processMarkdown(userMeetingMinutesTemplateName, variables);
            log.debug("meeting minutes: {}", answer);
        } catch (Exception e) {
            log.warn("process markdown error: {}", e.getMessage());
        }
        String docName = String.format("Meeting Minutes %s - %s", startDate, info.getSummary());
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
        pushDataDTO.setId(eventId);
        pushDataDTO.setAction("update");
        pushDataDTO.setSummary(event.getSummary());
        pushDataDTO.setDescription(event.getDescription());
        pushDataDTO.setKeywords(pushKeywords);
        pushDataDTO.setMeetingMinutesUrl(kbDoc.getDocUrl());
        pushDataDTO.setStatus("已结束");
        // 从数据库获取优先级和重要性
        pushDataDTO.setPriority("低");
        pushDataDTO.setImportance("高");
        pushDataDTO.setAllDay(event.getIsAllDay());
        pushDataDTO.setStartTime(event.getStart().getDateTime());
        pushDataDTO.setEndTime(event.getEnd().getDateTime());
        if (event.getLocation() != null) {
            pushDataDTO.setLocation(event.getLocation().getDisplayName());
        }
        pushDataDTO.setOrganizer(new IdDTO(event.getOrganizer().getId()));
        pushDataDTO.setAttendees(event.getAttendees().stream().map(attendee -> new IdDTO(attendee.getId())).toList());
        String content = objectMapper.writeValueAsString(pushDataDTO);
        log.debug("push data: {}", content);
        dingTalkService.pushEventToMultiDimensionalTable(userEventTableId, content);

        // TODO: 推送行动项到多为表

        return kbDoc.getDocUrl();
    }


    public void generateActionItems(String eventId) throws Exception {
    }

    public List<ScheduleEventDTO> getScheduleEvents() {
        return scheduleEventRepository.findAll().stream().map(DTOMapper.INSTANCE::toDto).toList();
    }

    public ScheduleEventDTO createScheduleEvent(ScheduleEventDTO scheduleEventDTO) throws Exception {
        ScheduleEventEntity entity = DTOMapper.INSTANCE.toEntity(scheduleEventDTO);
        entity.setId(null);
        ScheduleEventEntity save = scheduleEventRepository.save(entity);
        return DTOMapper.INSTANCE.toDto(save);
    }

    public ScheduleEventDTO updateScheduleEvent(ScheduleEventDTO scheduleEventDTO) throws Exception {
        ScheduleEventEntity exist = scheduleEventRepository.findById(scheduleEventDTO.getId())
                .orElseThrow(() -> new BusinessException("event not found"));

        ScheduleEventEntity update = DTOMapper.INSTANCE.partialUpdate(scheduleEventDTO, exist);
        ScheduleEventEntity save = scheduleEventRepository.save(update);
        return DTOMapper.INSTANCE.toDto(save);
    }

    public void deleteScheduleEvent(Long id) throws Exception {
        scheduleEventRepository.deleteById(id);
    }

    public void deleteScheduleEventByDingTalkId(String id) throws Exception {
        scheduleEventRepository.deleteByDingtalkEventId(id);
    }
}
