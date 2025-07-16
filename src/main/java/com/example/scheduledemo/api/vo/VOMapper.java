package com.example.scheduledemo.api.vo;

import com.example.scheduledemo.entity.MeetingMinutesDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VOMapper {

    VOMapper INSTANCE = Mappers.getMapper(VOMapper.class);

    MeetingMinutesDTO toDTO(MeetingMinutesAddVO vo);

    MeetingMinutesDTO toDTO(MeetingMinutesUpdateVO vo);

    MeetingMinutesVO toVO(MeetingMinutesDTO meetingMinutesDto);
}