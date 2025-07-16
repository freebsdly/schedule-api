package com.example.scheduledemo.service.dto;

import com.example.scheduledemo.entity.DepartmentEntity;
import com.example.scheduledemo.entity.EmployeeEntity;
import com.example.scheduledemo.feignclients.DeptBaseResponseDTO;
import com.example.scheduledemo.feignclients.EmployeeInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServiceDTOMapper {

    ServiceDTOMapper INSTANCE = Mappers.getMapper(ServiceDTOMapper.class);


    default DepartmentEntity toEntity(DeptBaseResponseDTO dto) {
        DepartmentEntity departmentEntity = new DepartmentEntity();

        departmentEntity.setName(dto.getName());
        departmentEntity.setDingTalkDepartmentId(dto.getDepartmentId());
        departmentEntity.setDingTalkParentId(dto.getParentId());

        return departmentEntity;
    }

    default EmployeeEntity toEntity(EmployeeInfoDTO dto) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        employeeEntity.setName(dto.getName());
        employeeEntity.setEmail(dto.getEmail());
        employeeEntity.setUnionId(dto.getUnionId());

        return employeeEntity;
    }

}
