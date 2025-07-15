package com.example.scheduledemo.service;

import com.example.scheduledemo.entity.EntityMapper;
import com.example.scheduledemo.entity.MeetingMinutesDto;
import com.example.scheduledemo.entity.MeetingMinutesEntity;
import com.example.scheduledemo.repository.MeetingMinutesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MeetingMinutesService {

    @Autowired
    private MeetingMinutesRepository meetingMinutesRepository;

    public MeetingMinutesDto getMeetingMinutesById(Long id) {
        MeetingMinutesEntity meetingMinutesEntity = meetingMinutesRepository.findById(id).orElse(null);
        return EntityMapper.INSTANCE.toDto(meetingMinutesEntity);
    }

    public List<MeetingMinutesDto> getMeetingMinutes() {
        List<MeetingMinutesEntity> meetingMinutesEntities = meetingMinutesRepository.findAll();
        return meetingMinutesEntities.stream().map(EntityMapper.INSTANCE::toDto).toList();
    }

    public MeetingMinutesDto createMeetingMinutes(MeetingMinutesDto meetingMinutesDto) {
        MeetingMinutesEntity entity = EntityMapper.INSTANCE.toEntity(meetingMinutesDto);
        MeetingMinutesEntity save = meetingMinutesRepository.save(entity);
        return EntityMapper.INSTANCE.toDto(save);
    }

    public MeetingMinutesDto updateMeetingMinutes(MeetingMinutesDto meetingMinutesDto) {
        MeetingMinutesEntity entity = EntityMapper.INSTANCE.toEntity(meetingMinutesDto);
        MeetingMinutesEntity save = meetingMinutesRepository.save(entity);
        return EntityMapper.INSTANCE.toDto(save);
    }

    public void deleteMeetingMinutes(Long id) {
        meetingMinutesRepository.deleteById(id);
    }
}
