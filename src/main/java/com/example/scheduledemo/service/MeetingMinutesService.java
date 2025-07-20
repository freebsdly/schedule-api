package com.example.scheduledemo.service;

import com.example.scheduledemo.exception.BusinessException;
import com.example.scheduledemo.repository.MeetingMinutesRepository;
import com.example.scheduledemo.repository.entity.MeetingMinutesEntity;
import com.example.scheduledemo.service.dto.DTOMapper;
import com.example.scheduledemo.service.dto.MeetingMinutesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MeetingMinutesService
{

    @Autowired
    private MeetingMinutesRepository meetingMinutesRepository;

    @Transactional(readOnly = true)
    public List<MeetingMinutesDTO> getMeetingMinutes()
    {
        List<MeetingMinutesEntity> meetingMinutesEntities = meetingMinutesRepository.findAll();
        return meetingMinutesEntities.stream().map(DTOMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    public MeetingMinutesDTO createMeetingMinutes(MeetingMinutesDTO meetingMinutesDto)
    {
        MeetingMinutesEntity entity = DTOMapper.INSTANCE.toEntity(meetingMinutesDto);
        entity.setId(null);
        MeetingMinutesEntity save = meetingMinutesRepository.save(entity);
        return DTOMapper.INSTANCE.toDto(save);
    }

    @Transactional
    public MeetingMinutesDTO updateMeetingMinutes(MeetingMinutesDTO meetingMinutesDto)
    {
        MeetingMinutesEntity exist = meetingMinutesRepository.findById(meetingMinutesDto.getId())
                .orElseThrow(() -> new BusinessException("Meeting Minutes not found"));
        MeetingMinutesEntity update = DTOMapper.INSTANCE.partialUpdate(meetingMinutesDto, exist);
        MeetingMinutesEntity save = meetingMinutesRepository.save(update);
        return DTOMapper.INSTANCE.toDto(save);
    }

    @Transactional
    public void deleteMeetingMinutes(Long id)
    {
        meetingMinutesRepository.deleteById(id);
    }
}
