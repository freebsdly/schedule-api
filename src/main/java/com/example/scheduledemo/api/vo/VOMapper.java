package com.example.scheduledemo.api.vo;

import com.example.scheduledemo.service.dto.EventAttendeeDTO;
import com.example.scheduledemo.service.dto.EventDTO;
import com.example.scheduledemo.service.dto.MeetingMinutesDTO;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VOMapper
{

    VOMapper INSTANCE = Mappers.getMapper(VOMapper.class);

    MeetingMinutesDTO toDTO(MeetingMinutesAddVO vo);

    MeetingMinutesDTO toDTO(MeetingMinutesUpdateVO vo);

    MeetingMinutesVO toVO(MeetingMinutesDTO meetingMinutesDto);

    @Mapping(source = "id", target = "employeeId")
    EventAttendeeDTO toDTO(IdVO vo);

    EventDTO.Create toDTO(EventVO.Create vo);

    EventDTO.Update toDTO(EventVO.Update vo);

    EventDTO.Delete toDTO(EventVO.Delete vo);

    EventDTO.Query toDTO(EventVO.Query vo);

    EventDTO.Create toCreateDTO(EventVO.Operate vo);

    EventDTO.Update toUpdateDTO(EventVO.Operate vo);

    EventDTO.Delete toDeleteDTO(EventVO.Operate vo);
}