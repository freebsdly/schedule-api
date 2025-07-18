package com.example.scheduledemo.api.vo;

import com.example.scheduledemo.service.dto.MeetingMinutesDTO;
import com.example.scheduledemo.service.dto.ScheduleEventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VOMapper {

    VOMapper INSTANCE = Mappers.getMapper(VOMapper.class);

    MeetingMinutesDTO toDTO(MeetingMinutesAddVO vo);

    MeetingMinutesDTO toDTO(MeetingMinutesUpdateVO vo);

    MeetingMinutesVO toVO(MeetingMinutesDTO meetingMinutesDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dingtalkEventId", source = "id")
    ScheduleEventDTO toDTO(OperateEventVO vo);

}