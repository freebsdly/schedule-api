package com.example.scheduledemo.entity;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    ActionItemEntity toEntity(ActionItemDTO actionItemDto);

    ActionItemDTO toDto(ActionItemEntity actionItemEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ActionItemEntity partialUpdate(ActionItemDTO actionItemDto, @MappingTarget ActionItemEntity actionItemEntity);

    MeetingMinutesEntity toEntity(MeetingMinutesDTO meetingMinutesDto);

    MeetingMinutesDTO toDto(MeetingMinutesEntity meetingMinutesEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MeetingMinutesEntity partialUpdate(MeetingMinutesDTO meetingMinutesDto, @MappingTarget MeetingMinutesEntity meetingMinutesEntity);
}