package com.hospitalApi.consults.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.hospitalApi.consults.dtos.EmployeeConsultResponseDTO;
import com.hospitalApi.consults.models.EmployeeConsult;

@Mapper(componentModel = "spring")
public interface EmployeeConsultMapper {

    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", source = "employee.firstName")
    @Mapping(target = "employeeLastName", source = "employee.lastName")
    @Mapping(target = "employeeType", source = "employee.employeeType.name")
    EmployeeConsultResponseDTO fromEmployeeConsultToResponse(EmployeeConsult employeeConsult);

    List<EmployeeConsultResponseDTO> fromEmployeeConsultsToResponse(List<EmployeeConsult> employeeConsults);
}
