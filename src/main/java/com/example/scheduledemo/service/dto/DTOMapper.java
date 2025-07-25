package com.example.scheduledemo.service.dto;

import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.example.scheduledemo.feignclients.EmployeeResponseDTO;
import com.example.scheduledemo.repository.entity.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);


    default DepartmentEntity toEntity(OapiV2DepartmentListsubResponse.DeptBaseResponse dto) {
        DepartmentEntity departmentEntity = new DepartmentEntity();

        departmentEntity.setName(dto.getName());
        departmentEntity.setDingTalkDepartmentId(dto.getDeptId());
        departmentEntity.setDingTalkParentId(dto.getParentId());

        return departmentEntity;
    }

    default EmployeeEntity toEntity(EmployeeResponseDTO.EmployeeInfoDTO dto) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(dto.getName());
        employeeEntity.setEmail(dto.getEmail());
        employeeEntity.setUnionId(dto.getUnionId());

        return employeeEntity;
    }


    DepartmentEntity toEntity(DepartmentDTO departmentDTO);

    DepartmentDTO toDto(DepartmentEntity departmentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DepartmentEntity partialUpdate(DepartmentDTO departmentDTO, @MappingTarget DepartmentEntity departmentEntity);

    DepartmentEntity toEntity(DepartmentEntity departmentEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    DepartmentEntity partialUpdate(DepartmentEntity source, @MappingTarget DepartmentEntity target);

    EmployeeEntity toEntity(EmployeeDTO employeeDTO);

    EmployeeDTO toDto(EmployeeEntity employeeEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    EmployeeEntity partialUpdate(EmployeeDTO employeeDTO, @MappingTarget EmployeeEntity employeeEntity);

    /**
     * Schedule Event Mapper
     */
    @Mapping(target="attendees", ignore = true)
    ScheduleEventEntity toCreateEntity(EventDTO.Create dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee.id", source = "employeeId")
    EventAttendeeEntity toCreateEntity(EventAttendeeDTO dto);

    ScheduleEventEntity toEntity(ScheduleEventDTO dto);

    EventDTO.Detail toDTO(ScheduleEventEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ScheduleEventEntity partialUpdate(EventDTO.Update dto, @MappingTarget ScheduleEventEntity entity);

    /**
     * Meeting Minutes Mapper
     */
    MeetingMinutesEntity toEntity(MeetingMinutesDTO meetingMinutesDto);

    MeetingMinutesDTO toDto(MeetingMinutesEntity meetingMinutesEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    MeetingMinutesEntity partialUpdate(MeetingMinutesDTO meetingMinutesDto,
                                       @MappingTarget MeetingMinutesEntity meetingMinutesEntity);

    @AfterMapping
    default void linkActionItems(@MappingTarget MeetingMinutesEntity meetingMinutesEntity) {
        meetingMinutesEntity.getActionItems().forEach(actionItem -> actionItem.setMeetingMinute(meetingMinutesEntity));
    }

    ActionItemEntity toEntity(ActionItemDTO actionItemDto);

    ActionItemDTO toDto(ActionItemEntity actionItemEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ActionItemEntity partialUpdate(ActionItemDTO actionItemDto, @MappingTarget ActionItemEntity actionItemEntity);


    ScheduleEventDTO toDTO(IntentRecognitionDTO dto);

    default EventAttendeeDTO toDTO(String value) {
        EventAttendeeDTO dto = new EventAttendeeDTO();
        dto.setEmployeeName(value);
        return dto;
    }
}
