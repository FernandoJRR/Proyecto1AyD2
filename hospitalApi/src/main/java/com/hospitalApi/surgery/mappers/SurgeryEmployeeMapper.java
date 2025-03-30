package com.hospitalApi.surgery.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.surgery.dtos.SurgeryEmpleoyeeResponseDTO;
import com.hospitalApi.surgery.models.SurgeryEmployee;

@Mapper(componentModel = "spring")
public interface SurgeryEmployeeMapper {

    @Mapping(target = "surgeryId", source = "surgery.id")
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "specialistEmployeeId", source = "specialistEmployee.id")
    @Mapping(target = "employeeName", source = "employee.firstName")
    @Mapping(target = "employeeLastName", source = "employee.lastName")
    public SurgeryEmpleoyeeResponseDTO fromSurgeryEmployeeToSurgeryEmpleoyeeResponseDTO(
            SurgeryEmployee surgeryEmployee);

    public List<SurgeryEmpleoyeeResponseDTO> fromSurgeryEmployeeListToSurgeryEmpleoyeeResponseDTOList(
            List<SurgeryEmployee> surgeryEmployees);
}
