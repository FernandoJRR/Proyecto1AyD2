package com.hospitalApi.employees.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.hospitalApi.employees.dtos.SpecialistEmpleoyeeResponseDTO;
import com.hospitalApi.employees.models.SpecialistEmployee;

@Mapper(componentModel = "spring")
public interface SpecialistEmployeeMapper {
    public SpecialistEmpleoyeeResponseDTO fromSpecialistEmployeeToSpecialistEmpleoyeeResponseDTO(
            SpecialistEmployee specialistEmployee);

    public List<SpecialistEmpleoyeeResponseDTO> fromSpecialistEmployeeListToSpecialistEmpleoyeeResponseDTOList(
            List<SpecialistEmployee> specialistEmployees);
}
