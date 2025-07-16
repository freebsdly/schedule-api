package com.example.scheduledemo.service;

import com.example.scheduledemo.entity.EntityMapper;
import com.example.scheduledemo.entity.MeetingMinutesDTO;
import com.example.scheduledemo.entity.MeetingMinutesEntity;
import com.example.scheduledemo.repository.MeetingMinutesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MeetingMinutesService {

    @Autowired
    private MeetingMinutesRepository meetingMinutesRepository;

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private AIService aiService;

    public MeetingMinutesDTO getMeetingMinutesById(Long id) {
        MeetingMinutesEntity meetingMinutesEntity = meetingMinutesRepository.findById(id).orElse(null);
        return EntityMapper.INSTANCE.toDto(meetingMinutesEntity);
    }

    public List<MeetingMinutesDTO> getMeetingMinutes() {
        List<MeetingMinutesEntity> meetingMinutesEntities = meetingMinutesRepository.findAll();
        return meetingMinutesEntities.stream().map(EntityMapper.INSTANCE::toDto).toList();
    }

    public MeetingMinutesDTO createMeetingMinutes(MeetingMinutesDTO meetingMinutesDto) {
        MeetingMinutesEntity entity = EntityMapper.INSTANCE.toEntity(meetingMinutesDto);
        MeetingMinutesEntity save = meetingMinutesRepository.save(entity);
        return EntityMapper.INSTANCE.toDto(save);
    }

    public MeetingMinutesDTO updateMeetingMinutes(MeetingMinutesDTO meetingMinutesDto) {
        MeetingMinutesEntity entity = EntityMapper.INSTANCE.toEntity(meetingMinutesDto);
        MeetingMinutesEntity save = meetingMinutesRepository.save(entity);
        return EntityMapper.INSTANCE.toDto(save);
    }

    public void deleteMeetingMinutes(Long id) {
        meetingMinutesRepository.deleteById(id);
    }

    @Tool(description = "generate Meeting Minutes")
    public String generateMeetingMinutes(
            @ToolParam(description = "user union id")
            String unionId,
            @ToolParam(description = "calendarId")
            String calendarId,
            @ToolParam(description = "event id")
            String eventId) throws Exception {
        String text = dingTalkService.getEventCloudRecordAllText(unionId, calendarId, eventId);
        return aiService.getMeetingMinutes(text);
    }
}
