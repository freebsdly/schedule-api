package com.example.scheduledemo.entity;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntityMapper {

    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    ActionItemEntity toEntity(ActionItemDto actionItemDto);

    ActionItemDto toDto(ActionItemEntity actionItemEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ActionItemEntity partialUpdate(ActionItemDto actionItemDto, @MappingTarget ActionItemEntity actionItemEntity);
}