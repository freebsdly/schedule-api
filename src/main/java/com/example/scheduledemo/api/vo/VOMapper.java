package com.example.scheduledemo.api.vo;

import com.example.scheduledemo.entity.MeetingMinutesDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VOMapper {

    VOMapper INSTANCE = Mappers.getMapper(VOMapper.class);

    MeetingMinutesDto toEntity(MeetingMinutesVO meetingMinutesVO);

    MeetingMinutesVO toDto(MeetingMinutesDto meetingMinutesDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MeetingMinutesDto partialUpdate(MeetingMinutesVO meetingMinutesVO, @MappingTarget MeetingMinutesDto meetingMinutesDto);
}